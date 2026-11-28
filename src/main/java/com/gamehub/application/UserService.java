package com.gamehub.application;

import com.gamehub.domain.Role;
import com.gamehub.domain.User;
import com.gamehub.infrastructure.Database;
import com.gamehub.infrastructure.UserRepository;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

/** Account creation and authentication backed by the production SQLite database. */
public final class UserService {
  private static final int PBKDF2_ITERATIONS = 120_000;
  private final Database db;
  private final UserRepository users;
  public UserService(Database db){this.db=db;this.users=new UserRepository(db.connection());}

  public boolean needsManagerSetup() throws SQLException {
    return !users.hasManager();
  }

  public void bootstrap(String username,String password)throws Exception {
    String login=normalizeLogin(username); validateCredentials(login,password); Connection c=db.connection(); c.setAutoCommit(false);
    try { if(!needsManagerSetup()) throw new IllegalStateException("Manager setup is already complete"); if(users.usernameExists(login)) throw new IllegalArgumentException("That username is already registered"); long id=users.insertUser(login,hash(password),Role.MANAGER.name(),Instant.now().toString()); audit(null,id,"MANAGER","BOOTSTRAPPED","Initial manager account"); c.commit(); }
    catch(Exception e){c.rollback();throw e;} finally{c.setAutoCommit(true);}
  }

  /** Legacy username-only registration retained for existing callers and fixtures. */
  public User registerCustomer(String username,String password)throws Exception {
    String login=normalizeLogin(username); validateCredentials(login,password); Connection c=db.connection(); c.setAutoCommit(false);
    try { if(users.usernameExists(login)) throw new IllegalArgumentException("That username is already registered"); long id=users.insertUser(login,hash(password),Role.CUSTOMER.name(),Instant.now().toString()); users.insertCustomer(id,login,null,null,Instant.now().toString()); audit(null,id,"CUSTOMER","REGISTERED","Customer account created"); c.commit(); return new User(id,login,Role.CUSTOMER); }
    catch(Exception e){c.rollback();throw e;} finally{c.setAutoCommit(true);}
  }

  public User registerCustomer(String fullName,String email,String phone,String password,String confirmation)throws Exception {
    if(fullName==null||fullName.trim().isEmpty()) throw new IllegalArgumentException("Full name is required");
    String normalizedEmail=normalizeEmail(email), normalizedPhone=normalizePhone(phone); validateCredentials(normalizedEmail,password);
    if(confirmation==null||!MessageDigest.isEqual(password.getBytes(java.nio.charset.StandardCharsets.UTF_8),confirmation.getBytes(java.nio.charset.StandardCharsets.UTF_8))) throw new IllegalArgumentException("Passwords do not match");
    Connection c=db.connection(); c.setAutoCommit(false);
    try { if(users.usernameExists(normalizedEmail)||users.customerIdentifierExists(normalizedEmail,normalizedPhone)) throw new IllegalArgumentException("An account already exists for that email or phone"); long id=users.insertUser(normalizedEmail,hash(password),Role.CUSTOMER.name(),Instant.now().toString()); users.insertCustomer(id,fullName.trim(),normalizedEmail,normalizedPhone,Instant.now().toString()); audit(null,id,"CUSTOMER","REGISTERED","Customer account created"); c.commit(); return new User(id,normalizedEmail,Role.CUSTOMER); }
    catch(Exception e){c.rollback();throw e;} finally{c.setAutoCommit(true);}
  }
  /** Compatibility overload used by existing application fixtures. */
  public User registerCustomer(String fullName,String email,String phone,String password)throws Exception{return registerCustomer(fullName,email,phone,password,password);}

  /** Manager-authorized operational account provisioning used by the GUI. */
  public User provision(User actor,String username,String temporaryPassword,Role role)throws Exception {
    if(actor==null||actor.role()!=Role.MANAGER) throw new SecurityException("Manager authorization is required");
    if(role!=Role.STAFF&&role!=Role.ORGANIZER) throw new IllegalArgumentException("Only staff and organizer accounts may be provisioned");
    String login=normalizeLogin(username); validateCredentials(login,temporaryPassword); Connection c=db.connection(); c.setAutoCommit(false);
    try { if(users.usernameExists(login)) throw new IllegalArgumentException("That username is already registered"); long id=users.insertUser(login,hash(temporaryPassword),role.name(),Instant.now().toString()); audit(actor.id(),id,role.name(),"PROVISIONED","Manager-created operational account"); c.commit(); return new User(id,login,role); }
    catch(Exception e){c.rollback();throw e;} finally{c.setAutoCommit(true);}
  }

  /** Compatibility overload for existing service callers. */
  public User provision(String actorUsername,String password,String username,String temporaryPassword,Role role)throws Exception{return provision(authenticate(actorUsername,password),username,temporaryPassword,role);}

  private void audit(Long actorId,long entityId,String type,String operation,String reason)throws SQLException {try(PreparedStatement p=db.connection().prepareStatement("INSERT INTO audit_events(actor_user_id,entity_type,entity_id,operation,reason,created_at) VALUES(?,?,?,?,?,?)")){if(actorId==null)p.setNull(1,Types.INTEGER);else p.setLong(1,actorId);p.setString(2,type);p.setLong(3,entityId);p.setString(4,operation);p.setString(5,reason);p.setString(6,Instant.now().toString());p.executeUpdate();}}
  public User authenticate(String username,String password)throws SQLException {if(username==null||username.isBlank()||password==null)return null;String login=normalizeLogin(username),stored=users.passwordHash(login);if(stored==null||!verify(password,stored))return null;return users.findActive(login);}
  private static String normalizeLogin(String value){return value==null?"":value.trim().toLowerCase(Locale.ROOT);}
  private static String normalizeEmail(String value){String email=normalizeLogin(value);if(!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))throw new IllegalArgumentException("Enter a valid email address");return email;}
  private static String normalizePhone(String value){if(value==null||value.isBlank())throw new IllegalArgumentException("Phone is required");String phone=value.trim().replaceAll("[()\\-\\s]","");if(!phone.matches("^\\+?[0-9]{7,15}$"))throw new IllegalArgumentException("Enter a valid phone number");return phone;}
  private static void validateCredentials(String login,String password){if(login.isBlank()||password==null||password.length()<8)throw new IllegalArgumentException("Username and password (8+ characters) are required");}
  private static String hash(String password)throws Exception{byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);return Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(derive(password,salt));}
  private static boolean verify(String password,String stored){try{String[] x=stored.split(":",-1);if(x.length!=2)return false;return MessageDigest.isEqual(derive(password,Base64.getDecoder().decode(x[0])),Base64.getDecoder().decode(x[1]));}catch(Exception e){return false;}}
  private static byte[] derive(String password,byte[] salt)throws Exception{PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,PBKDF2_ITERATIONS,256);try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}finally{spec.clearPassword();}}
}
