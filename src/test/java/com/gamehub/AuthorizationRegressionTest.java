package com.gamehub;
import com.gamehub.application.*; import com.gamehub.infrastructure.*; import org.junit.jupiter.api.*; import java.nio.file.*; import static org.junit.jupiter.api.Assertions.*;
class AuthorizationRegressionTest {
 @Test void actorlessRestrictedOperationsAreRejectedWithoutChangingState() throws Exception {Path p=Files.createTempFile("auth-reg-", ".db");try(Database db=new Database(p)){var us=new UserService(db);us.bootstrap("manager","password1");var manager=us.authenticate("manager","password1");var o=new OperationsService(db);assertThrows(SecurityException.class,()->o.addStation(0,"X-1","PC","",50000));assertEquals(0,o.reportCount(manager.id(),"gaming_stations"));assertThrows(SecurityException.class,()->o.addStation(-1,"X-2","PC","",50000));assertEquals(0,o.reportCount(manager.id(),"gaming_stations"));}finally{Files.deleteIfExists(p);}}
}

