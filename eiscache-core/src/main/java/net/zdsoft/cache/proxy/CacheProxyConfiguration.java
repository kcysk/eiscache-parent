package net.zdsoft.cache.proxy;

import net.zdsoft.cache.interceptor.CacheInterceptor;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.09.06
 */
//@Configuration
public class CacheProxyConfiguration {

    @Bean(name = "org.zdsoft.cache.proxy.internalCacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor, CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        cacheInterceptor.setActiveModel(AdviceMode.PROXY);
        advisor.setAdvice(cacheInterceptor);
        ((CacheBeanFactoryPointCutAdvisor) advisor).setClassFilter(new ClassFilterAdapter(filter));
        return advisor;
    }

    @Bean
    public CacheInterceptor cacheInterceptor(TypeDescriptor typeDescriptor){
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        cacheInterceptor.setTypeDescriptor(typeDescriptor);
        return cacheInterceptor;
    }

    @Bean
    public CacheOperationParser cacheOperationParser() {
        return new CacheOperationParser();
    }
}
