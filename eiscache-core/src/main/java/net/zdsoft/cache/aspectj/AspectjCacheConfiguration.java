package net.zdsoft.cache.aspectj;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.08.30
 */
@Configuration
public class AspectjCacheConfiguration {

    @Bean(name = "net.zdsoft.cache.aspect.exCacheAspectj")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheAspectj cacheAspect() {
        CacheAspectj aspectj = new CacheAspectj();
        aspectj.setActiveModel(AdviceMode.ASPECTJ);
        return aspectj;
    }

}
