package com.gamehub;
import com.gamehub.application.UserService; import com.gamehub.infrastructure.Database; import org.junit.jupiter.api.Test; import java.nio.file.*; import static org.junit.jupiter.api.Assertions.*;
class CustomerAccountBossTest {
 @Test void customerAccountSurvivesRestartAndAuthenticates() throws Exception {Path p=Files.createTempFile("customer-", ".db");try{try(Database db=new Database(p)){UserService s=new UserService(db);var created=s.registerCustomer("gamer01","strong-pass");assertEquals("CUSTOMER",created.role().name());}try(Database reopened=new Database(p)){var authenticated=new UserService(reopened).authenticate("gamer01","strong-pass");assertNotNull(authenticated);assertEquals("CUSTOMER",authenticated.role().name());assertNull(new UserService(reopened).authenticate("gamer01","wrong-pass"));}}finally{Files.deleteIfExists(p);}}
}
