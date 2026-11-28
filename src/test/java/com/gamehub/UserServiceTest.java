package com.gamehub;
import com.gamehub.application.UserService; import com.gamehub.domain.User; import com.gamehub.infrastructure.Database; import org.junit.jupiter.api.Test; import java.nio.file.*; import static org.junit.jupiter.api.Assertions.*;
class UserServiceTest {
 @Test void managerBootstrapAndAuthenticationPersist() throws Exception { Path db=Files.createTempFile("gamehub-test-", ".db"); try(Database database=new Database(db)){UserService service=new UserService(database);service.bootstrap("manager","correct-horse");User user=service.authenticate("manager","correct-horse");assertNotNull(user);assertEquals("MANAGER",user.role().name());assertNull(service.authenticate("manager","wrong-password"));} finally {Files.deleteIfExists(db);}}
}
