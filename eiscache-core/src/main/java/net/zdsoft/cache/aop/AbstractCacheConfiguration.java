package net.zdsoft.cache.aop;

import net.zdsoft.cache.aop.interceptor.CacheAopExecutor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import net.zdsoft.cache.aop.proxy.CacheInterceptor;
import org.apache.log4j.Logger;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author shenke
 * @since 2017.10.12
 */
public abstract class AbstractCacheConfiguration {

    private Logger logger = Logger.getLogger(AbstractCacheConfiguration.class);

    @Bean
    public CacheOperationParser cacheOperationParser() {
        return new CacheOperationParser();
    }

    @Autowired
    public void setAopExecutor(CacheAopExecutor aopExecutor,
                               @Value(value = "eis-cache.slowCache") long slowCache,
                               @Value(value = "eis-cache.slowInvoke") long slowInvoke) {
        aopExecutor.setSlowCacheTime(slowCache);
        aopExecutor.setSlowInvokeTime(slowInvoke);
    }

    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor,
                                                           CacheOperationParser cacheOperationParser,
                                                           DynamicCacheClassFilter filter) {
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        if ( advisor != null ) {
            advisor.setAdvice(cacheInterceptor);
        } else {
            logger.warn(" cache interceptor is null ensure cache mode is ASPECTJ ");
        }
        ((CacheBeanFactoryPointCutAdvisor) advisor).setClassFilter(new ClassFilterAdapter(filter));
        return advisor;
    }

}
