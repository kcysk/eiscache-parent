package net.zdsoft.cache.aspectj;

import net.zdsoft.cache.config.AbstractCacheConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.08.30
 */
@Configuration
public class AspectjCacheConfiguration extends AbstractCacheConfiguration {

    @Bean(name = "net.zdsoft.cache.aspect.exCacheAspectj")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheAspectj cacheAspect() {
        return new CacheAspectj();
    }

}
