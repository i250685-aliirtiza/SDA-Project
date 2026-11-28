package com.gamehub.infrastructure;
import java.nio.file.*; import java.sql.*; import java.io.*;
public final class Database implements AutoCloseable {
 private final Connection connection;
 public Database() throws SQLException { this(resolvePath()); }
 public Database(Path path) throws SQLException {
   Path absolute = path.toAbsolutePath();
   try {
     if (absolute.getParent() != null) Files.createDirectories(absolute.getParent());
   } catch (IOException failure) {
     throw new SQLException("Could not create the database directory", failure);
   }
   connection = DriverManager.getConnection("jdbc:sqlite:" + absolute);
   try {
     try (Statement statement = connection.createStatement()) {
       statement.execute("PRAGMA foreign_keys=ON");
       statement.execute("PRAGMA busy_timeout=5000");
       statement.execute("PRAGMA journal_mode=WAL");
     }
     connection.setAutoCommit(false);
     migrate();
     connection.commit();
     connection.setAutoCommit(true);
   } catch (SQLException | RuntimeException failure) {
     try {
       if (!connection.getAutoCommit()) connection.rollback();
     } catch (SQLException rollbackFailure) {
       failure.addSuppressed(rollbackFailure);
     }
     try {
       connection.close();
     } catch (SQLException closeFailure) {
       failure.addSuppressed(closeFailure);
     }
     throw failure;
   }
 }
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
   ensureColumn("team_members", "joined_at", "TEXT NOT NULL DEFAULT ''");
   ensureColumn("registrations", "fee_status", "TEXT NOT NULL DEFAULT 'PAID'");
   ensureColumn("registrations", "created_at", "TEXT NOT NULL DEFAULT ''");
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
     if (tableExists("team_members")) s.executeUpdate("UPDATE team_members SET joined_at=CURRENT_TIMESTAMP WHERE joined_at='' OR joined_at IS NULL");
     if (tableExists("registrations")) s.executeUpdate("UPDATE registrations SET created_at=CURRENT_TIMESTAMP WHERE created_at='' OR created_at IS NULL");
     if (tableExists("sessions") && tableExists("reservations")) s.executeUpdate("UPDATE sessions SET user_id=(SELECT user_id FROM reservations r WHERE r.id=sessions.reservation_id), station_id=(SELECT station_id FROM reservations r WHERE r.id=sessions.reservation_id) WHERE user_id IS NULL OR station_id IS NULL");
   }
   try(InputStream in=getClass().getResourceAsStream("/db/migrations/V1__init.sql")){
     if(in==null) throw new SQLException("Migration resource missing");
     String sql=new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
     try(Statement s=connection.createStatement()){
       for(String part:sql.split(";")) if(!part.isBlank()) s.execute(part);
     }
   }catch(IOException e){throw new SQLException(e);}
   ensureColumn("sessions", "hourly_rate_paisa", "INTEGER CHECK(hourly_rate_paisa > 0)");
   try (Statement statement = connection.createStatement()) {
     statement.executeUpdate("CREATE TABLE IF NOT EXISTS schema_migrations (version INTEGER PRIMARY KEY)");
     // Existing active sessions have no historical rate. Preserve their current billing basis.
     statement.executeUpdate("""
         UPDATE sessions SET hourly_rate_paisa=(
           SELECT hourly_rate_paisa FROM gaming_stations WHERE id=sessions.station_id AND hourly_rate_paisa > 0
         ) WHERE status='ACTIVE' AND hourly_rate_paisa IS NULL
         """);
     statement.executeUpdate("INSERT OR IGNORE INTO schema_migrations(version) VALUES (1), (2)");
   }
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

