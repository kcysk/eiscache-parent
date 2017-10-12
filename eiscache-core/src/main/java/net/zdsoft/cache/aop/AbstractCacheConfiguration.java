package net.zdsoft.cache.aop;

import net.zdsoft.cache.Constant;
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
    public void setSlowTime(@Value(value = "${" + Constant.SLOW_CACHE_NAME + "}") long slowCache,
                           @Value(value = "${" + Constant.SLOW_INVOKE_NAME + "}") long slowInvoke,
                           CacheAopExecutor aopExecutor) {
        aopExecutor.setSlowInvokeTime(slowInvoke);
        aopExecutor.setSlowCacheTime(slowCache);
    }

    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor,
                                                           CacheOperationParser cacheOperationParser,
                                                           DynamicCacheClassFilter filter) {
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        if ( cacheInterceptor != null ) {
            advisor.setAdviceBeanName("net.zdsoft.cache.aop.proxy.CacheInterceptor");
        } else {
            logger.warn(" cache interceptor is null ensure cache mode is ASPECTJ ");
        }
        ((CacheBeanFactoryPointCutAdvisor) advisor).setClassFilter(new ClassFilterAdapter(filter));
        return advisor;
    }

}
