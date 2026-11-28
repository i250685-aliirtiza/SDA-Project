package com.gamehub.infrastructure;

import com.gamehub.domain.Role;
import com.gamehub.domain.User;
import java.sql.*;
import java.util.Objects;

/** Persistence boundary for user and customer records. */
public final class UserRepository {
  private final Connection connection;
  public UserRepository(Connection connection){this.connection=Objects.requireNonNull(connection);}
  public boolean hasManager() throws SQLException {
    try (var statement = connection.prepareStatement("SELECT 1 FROM users WHERE role='MANAGER' LIMIT 1");
         var result = statement.executeQuery()) {
      return result.next();
    }
  }
  public boolean hasUsers() throws SQLException {try(var q=connection.prepareStatement("SELECT COUNT(*) FROM users");var r=q.executeQuery()){return r.next()&&r.getInt(1)>0;}}
  public long insertUser(String username,String passwordHash,String role,String createdAt)throws SQLException {try(var p=connection.prepareStatement("INSERT INTO users(username,password_hash,role,created_at) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){p.setString(1,username);p.setString(2,passwordHash);p.setString(3,role);p.setString(4,createdAt);p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();return k.getLong(1);}}}
  public void insertCustomer(long userId,String fullName,String email,String phone,String createdAt)throws SQLException {try(var p=connection.prepareStatement("INSERT INTO customers(user_id,full_name,email,phone,created_at) VALUES(?,?,?,?,?)")){p.setLong(1,userId);p.setString(2,fullName);p.setString(3,email);p.setString(4,phone);p.setString(5,createdAt);p.executeUpdate();}}
  public boolean customerIdentifierExists(String email,String phone)throws SQLException {try(var p=connection.prepareStatement("SELECT 1 FROM customers WHERE (email IS NOT NULL AND email=?) OR (phone IS NOT NULL AND phone=?) LIMIT 1")){p.setString(1,email);p.setString(2,phone);try(var r=p.executeQuery()){return r.next();}}}
  public boolean usernameExists(String username)throws SQLException {try(var p=connection.prepareStatement("SELECT 1 FROM users WHERE username=? LIMIT 1")){p.setString(1,username);try(var r=p.executeQuery()){return r.next();}}}
  public User findActive(String username)throws SQLException {try(var p=connection.prepareStatement("SELECT id,username,role,password_hash FROM users WHERE username=? AND active=1")){p.setString(1,username);try(var r=p.executeQuery()){return r.next()?new User(r.getLong(1),r.getString(2),Role.valueOf(r.getString(3))):null;}}}
  public String passwordHash(String username)throws SQLException {try(var p=connection.prepareStatement("SELECT password_hash FROM users WHERE username=? AND active=1")){p.setString(1,username);try(var r=p.executeQuery()){return r.next()?r.getString(1):null;}}}
}
