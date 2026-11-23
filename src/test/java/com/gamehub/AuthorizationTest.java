package com.gamehub;

import com.gamehub.application.OperationsService;
import com.gamehub.application.UserService;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class AuthorizationTest {
  @Test void managerOnlyStationAdministrationRejectsCustomer() throws Exception {
    var path=Files.createTempFile("auth-", ".db");
    try (var db=new Database(path)) {
      var users=new UserService(db); users.bootstrap("manager","manager-pass");
      var manager=users.authenticate("manager","manager-pass");
      var customer=users.registerCustomer("customer","customer-pass");
      var ops=new OperationsService(db);
      assertDoesNotThrow(()->ops.addStation(manager.id(),"AUTH-PC","PC","",50000));
      assertThrows(SecurityException.class,()->ops.addStation(customer.id(),"AUTH-PC-2","PC","",50000));
    } finally { Files.deleteIfExists(path); }
  }
}
