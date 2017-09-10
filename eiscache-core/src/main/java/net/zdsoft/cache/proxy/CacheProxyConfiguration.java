package net.zdsoft.cache.proxy;

import net.zdsoft.cache.interceptor.CacheInterceptor;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.09.06
 */
@Configuration
public class CacheProxyConfiguration {

    @Bean(name = "org.zdsoft.cache.proxy.internalCacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor() {
        CacheOperationParser cacheOperationParser = cacheOperationParser();
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        CacheInterceptor interceptor = cacheInterceptor();
        interceptor.setActiveModel(AdviceMode.PROXY);
        advisor.setAdvice(interceptor);
        return advisor;
    }

    @Bean
    public CacheInterceptor cacheInterceptor(){
        return new CacheInterceptor();
    }

    @Bean
    public CacheOperationParser cacheOperationParser() {
        return new CacheOperationParser();
    }
}
