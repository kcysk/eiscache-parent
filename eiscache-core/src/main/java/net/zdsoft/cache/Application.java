package net.zdsoft.cache;

import net.zdsoft.cache.annotation.EnableCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;

/**
 * @author shenke
 * @since 17-9-10下午1:04
 */
@SpringBootApplication
@EnableCache(mode = AdviceMode.PROXY)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
