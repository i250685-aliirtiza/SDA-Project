package com.gamehub.infrastructure;
import java.nio.file.*; import java.sql.*; import java.io.*;
public final class Database implements AutoCloseable {
 private final Connection connection;
 public Database() throws SQLException { this(resolvePath()); }
 public Database(Path p) throws SQLException { try { Path abs=p.toAbsolutePath(); if(abs.getParent()!=null)Files.createDirectories(abs.getParent()); connection=DriverManager.getConnection("jdbc:sqlite:"+abs); try(Statement s=connection.createStatement()){s.execute("PRAGMA foreign_keys=ON");s.execute("PRAGMA busy_timeout=5000");s.execute("PRAGMA journal_mode=WAL");} migrate(); } catch(IOException e){throw new SQLException(e);} }
 private static Path resolvePath(){String configured=System.getenv("GAMEHUB_DB_PATH");return Path.of(configured==null||configured.isBlank()?Path.of(System.getProperty("user.home"),".gamehub","gamehub.db").toString():configured);}
 private void migrate() throws SQLException {
   // V1 is also used to upgrade databases created by the original prototype.
   // CREATE TABLE IF NOT EXISTS cannot add columns to those legacy tables, so
   // add the compatible columns first and keep every existing row intact.
   ensureColumn("users", "active", "INTEGER NOT NULL DEFAULT 1");
   ensureColumn("tournaments", "capacity", "INTEGER NOT NULL DEFAULT 32");
   ensureColumn("tournaments", "entrant_type", "TEXT NOT NULL DEFAULT 'INDIVIDUAL'");
   ensureColumn("tournaments", "game", "TEXT NOT NULL DEFAULT 'Gaming'");
   ensureColumn("tournaments", "registration_open_at", "TEXT");
   ensureColumn("tournaments", "registration_close_at", "TEXT");
   ensureColumn("tournaments", "scheduled_start_at", "TEXT");
   ensureColumn("tournaments", "eligibility", "TEXT NOT NULL DEFAULT ''");
   ensureColumn("gaming_stations", "specs", "TEXT NOT NULL DEFAULT ''");
   ensureColumn("gaming_stations", "active", "INTEGER NOT NULL DEFAULT 1");
   ensureColumn("reservations", "created_at", "TEXT NOT NULL DEFAULT ''");
   ensureColumn("team_members", "role", "TEXT NOT NULL DEFAULT 'MEMBER'");
   ensureColumn("team_members", "joined_at", "TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP");
   ensureColumn("registrations", "fee_status", "TEXT NOT NULL DEFAULT 'PAID'");
   ensureColumn("registrations", "created_at", "TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP");
   ensureColumn("sessions", "user_id", "INTEGER");
   ensureColumn("sessions", "station_id", "INTEGER");
   ensureColumn("sessions", "planned_end_at", "TEXT");
   ensureColumn("sessions", "charge_id", "INTEGER");
   ensureColumn("payments", "charge_id", "INTEGER");
   ensureColumn("payments", "idempotency_key", "TEXT");
   ensureColumn("maintenance", "resolution_notes", "TEXT");
   ensureColumn("maintenance", "reported_by", "INTEGER");
   // Backfill columns introduced to sessions from their reservation.
   try (Statement s = connection.createStatement()) {
     if (tableExists("reservations")) s.executeUpdate("UPDATE reservations SET created_at=CURRENT_TIMESTAMP WHERE created_at='' OR created_at IS NULL");
     if (tableExists("sessions") && tableExists("reservations")) s.executeUpdate("UPDATE sessions SET user_id=(SELECT user_id FROM reservations r WHERE r.id=sessions.reservation_id), station_id=(SELECT station_id FROM reservations r WHERE r.id=sessions.reservation_id) WHERE user_id IS NULL OR station_id IS NULL");
   }
   try(InputStream in=getClass().getResourceAsStream("/db/migrations/V1__init.sql")){
     if(in==null) throw new SQLException("Migration resource missing");
     String sql=new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
     try(Statement s=connection.createStatement()){
       for(String part:sql.split(";")) if(!part.isBlank()) s.execute(part);
     }
   }catch(IOException e){throw new SQLException(e);}
 }
 private void ensureColumn(String table, String column, String definition) throws SQLException {
   boolean tableExists=tableExists(table);
   if (!tableExists) return;
   try (Statement s=connection.createStatement(); ResultSet r=s.executeQuery("PRAGMA table_info("+table+")")) {
     while(r.next()) if(column.equalsIgnoreCase(r.getString("name"))) return;
   }
   try (Statement s=connection.createStatement()) { s.executeUpdate("ALTER TABLE "+table+" ADD COLUMN "+column+" "+definition); }
 }
 private boolean tableExists(String table) throws SQLException {
   try (PreparedStatement p=connection.prepareStatement("SELECT 1 FROM sqlite_master WHERE type='table' AND name=?")) {
     p.setString(1, table); try (ResultSet r=p.executeQuery()) { return r.next(); }
   }
 }
 public Connection connection(){return connection;} public void close() throws SQLException{connection.close();}
}

