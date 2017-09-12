package net.zdsoft.cache;

import net.zdsoft.cache.entity.User;
import net.zdsoft.cache.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * @author shenke
 * @since 17-9-10下午1:55
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JpaTest {

    @Autowired
    private UserService userService;

    @Test
    public void testUserDao () {
        User user = new User();
        user.setId(UUID.randomUUID().toString().substring(0,32));
        user.setUserName("test");
        user.setPassword("ttt");
        userService.save(user);
    }

    @Test
    public void testFind() {
        User user = userService.findOne("31b9e149-7af8-4d53-803d-5128f8e0");
        System.out.printf(user.getUserName());
    }
}
