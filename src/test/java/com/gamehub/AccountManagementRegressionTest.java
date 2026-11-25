package com.gamehub;

import com.gamehub.application.UserService;
import com.gamehub.domain.Role;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class AccountManagementRegressionTest {
  @Test void registrationNormalizesIdentifiersAndAuditsOnlyCommittedRows() throws Exception {
    Path path=Files.createTempFile("gamehub-account-", ".db");
    try(Database db=new Database(path)) {
      UserService users=new UserService(db);
      var customer=users.registerCustomer("A Gamer", " Gamer@Example.com ", "+92 300-1234567", "correct-horse", "correct-horse");
      assertEquals("gamer@example.com", customer.username());
      assertThrows(IllegalArgumentException.class, () -> users.registerCustomer("Other", "GAMER@example.com", "+923001234567", "correct-horse", "correct-horse"));
      try(ResultSet rows=db.connection().createStatement().executeQuery("SELECT COUNT(*) FROM users WHERE role='CUSTOMER'")){assertTrue(rows.next());assertEquals(1,rows.getInt(1));}
      try(ResultSet rows=db.connection().createStatement().executeQuery("SELECT COUNT(*) FROM audit_events WHERE operation='REGISTERED'")){assertTrue(rows.next());assertEquals(1,rows.getInt(1));}
      assertNotNull(users.authenticate(" GAMER@EXAMPLE.COM ", "correct-horse"));
      assertNull(users.authenticate("gamer@example.com", "wrong-pass"));
    } finally { Files.deleteIfExists(path); }
  }

  @Test void managerCanProvisionOperationalRolesAndOthersCannot() throws Exception {
    Path path=Files.createTempFile("gamehub-provision-", ".db");
    try(Database db=new Database(path)) {
      UserService users=new UserService(db);
      users.bootstrap("manager", "correct-horse");
      var manager=users.authenticate("manager", "correct-horse");
      var staff=users.provision(manager, "frontdesk", "staff-pass", Role.STAFF);
      var organizer=users.provision(manager, "brackets", "organizer-pass", Role.ORGANIZER);
      assertEquals(Role.STAFF, staff.role());
      assertEquals(Role.ORGANIZER, organizer.role());
      assertThrows(SecurityException.class, () -> users.provision(staff, "another", "staff-pass", Role.STAFF));
      assertThrows(IllegalArgumentException.class, () -> users.provision(manager, "frontdesk", "other-pass", Role.STAFF));
      assertNotNull(users.authenticate("frontdesk", "staff-pass"));
      assertNotNull(users.authenticate("brackets", "organizer-pass"));
    } finally { Files.deleteIfExists(path); }
  }
}
