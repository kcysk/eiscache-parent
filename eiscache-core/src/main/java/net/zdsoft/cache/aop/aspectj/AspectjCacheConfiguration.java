package net.zdsoft.cache.aop.aspectj;

import net.zdsoft.cache.aop.AbstractCacheConfiguration;
import net.zdsoft.cache.aop.CacheBeanFactoryPointCutAdvisor;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.08.30
 */
public class AspectjCacheConfiguration extends AbstractCacheConfiguration {

    @Bean(name = "net.zdsoft.cache.aspect.exCacheAspectj")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheAspectj cacheAspect(CacheBeanFactoryPointCutAdvisor advisor, TypeDescriptor typeDescriptor) {
        CacheAspectj aspectj = new CacheAspectj();
        aspectj.setAdvisor(advisor);
        aspectj.setTypeDescriptor(typeDescriptor);
        aspectj.setAdvisor(advisor);
        setSlowTime(aspectj);
        return aspectj;
    }
}
