package net.zdsoft.cache.proxy;

import net.zdsoft.cache.interceptor.CacheInterceptor;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;

/**
 * @author shenke
 * @since 2017.09.06
 */
//@Configuration
public class CacheProxyConfiguration implements EnvironmentAware {

    private Environment environment;

    @Bean(name = "org.zdsoft.cache.proxy.internalCacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor, CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        advisor.setAdvice(cacheInterceptor);
        ((CacheBeanFactoryPointCutAdvisor) advisor).setClassFilter(new ClassFilterAdapter(filter));
        return advisor;
    }

    @Bean
    public CacheInterceptor cacheInterceptor(TypeDescriptor typeDescriptor, CacheOperationParser parser){
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        cacheInterceptor.setTypeDescriptor(typeDescriptor);

        String slowCache = environment.getProperty("eiscache.slowCache");
        String slowInvoke = environment.getProperty("eiscache.slowInvoke");
        cacheInterceptor.setSlowCacheTime(NumberUtils.toLong(slowCache, Long.MAX_VALUE));
        cacheInterceptor.setSlowInvokeTime(NumberUtils.toLong(slowInvoke, Long.MAX_VALUE));
        cacheInterceptor.setCacheOperationParser(parser);
        return cacheInterceptor;
    }

    @Bean
    public CacheOperationParser cacheOperationParser() {
        return new CacheOperationParser();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
