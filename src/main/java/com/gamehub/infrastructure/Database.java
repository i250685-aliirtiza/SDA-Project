package com.gamehub.infrastructure;
import java.nio.file.*; import java.sql.*; import java.io.*;
public final class Database implements AutoCloseable {
 private final Connection connection;
 public Database() throws SQLException { this(resolvePath()); }
 public Database(Path p) throws SQLException { try { Path abs=p.toAbsolutePath(); if(abs.getParent()!=null)Files.createDirectories(abs.getParent()); connection=DriverManager.getConnection("jdbc:sqlite:"+abs); try(Statement s=connection.createStatement()){s.execute("PRAGMA foreign_keys=ON");s.execute("PRAGMA busy_timeout=5000");s.execute("PRAGMA journal_mode=WAL");} migrate(); } catch(IOException e){throw new SQLException(e);} }
 private static Path resolvePath(){String configured=System.getenv("GAMEHUB_DB_PATH");return Path.of(configured==null||configured.isBlank()?Path.of(System.getProperty("user.home"),".gamehub","gamehub.db").toString():configured);}
 private void migrate() throws SQLException { try(InputStream in=getClass().getResourceAsStream("/db/migrations/V1__init.sql")){ if(in==null) throw new SQLException("Migration resource missing"); String sql=new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8); try(Statement s=connection.createStatement()){for(String part:sql.split(";")) if(!part.isBlank())s.execute(part);}}catch(IOException e){throw new SQLException(e);} }
 public Connection connection(){return connection;} public void close() throws SQLException{connection.close();}
}
