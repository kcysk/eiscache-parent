package net.zdsoft.cache.proxy;

import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.interceptor.CacheInterceptor;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author shenke
 * @since 2017.09.06
 */
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
