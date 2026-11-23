package com.gamehub.infrastructure;
import java.nio.file.*; import java.sql.*; import java.io.*; import java.util.*;
public final class Database implements AutoCloseable {
 private final Connection connection;
 public Database() throws SQLException { String configured=System.getenv("GAMEHUB_DB_PATH"); Path p=Path.of(configured==null||configured.isBlank()?Path.of(System.getProperty("user.home"),".gamehub","gamehub.db").toString():configured); try { Files.createDirectories(p.toAbsolutePath().getParent()); } catch(IOException e){throw new SQLException(e);} connection=DriverManager.getConnection("jdbc:sqlite:"+p); connection.createStatement().execute("PRAGMA foreign_keys=ON"); migrate(); }
 private void migrate() throws SQLException { try(InputStream in=getClass().getResourceAsStream("/db/migrations/V1__init.sql")){ if(in==null) throw new SQLException("Migration resource missing"); String sql=new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8); for(String s:sql.split(";")) if(!s.isBlank()) connection.createStatement().execute(s); } catch(IOException e){throw new SQLException(e);} }
 public Connection connection(){return connection;} public void close() throws SQLException {connection.close();}
}
