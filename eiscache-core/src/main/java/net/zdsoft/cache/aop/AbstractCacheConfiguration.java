package net.zdsoft.cache.aop;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.aop.interceptor.CacheAopExecutor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import net.zdsoft.cache.aop.proxy.CacheInterceptor;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author shenke
 * @since 2017.10.11
 */
public abstract class AbstractCacheConfiguration implements EnvironmentAware {

    protected Logger logger = Logger.getLogger(AbstractCacheConfiguration.class);
    protected Environment environment;

    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor, CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        if ( cacheInterceptor != null ) {
            advisor.setAdvice(cacheInterceptor);
        } else {
            logger.warn("cacheInterceptor is null sure you cache mode is aspectj");
        }
        ((CacheBeanFactoryPointCutAdvisor) advisor).setClassFilter(new ClassFilterAdapter(filter));
        return advisor;
    }

    @Bean
    public CacheOperationParser cacheOperationParser() {
        return new CacheOperationParser();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected void setSlowTime(CacheAopExecutor aopExecutor) {
        String slowCache = environment.getProperty(Constant.SLOW_CACHE_NAME);
        String slowInvoke = environment.getProperty(Constant.SLOW_INVOKE_NAME);
        aopExecutor.setSlowCacheTime(NumberUtils.toLong(slowCache, Constant.DEFAULT_SLOW_CACHE));
        aopExecutor.setSlowInvokeTime(NumberUtils.toLong(slowInvoke, Constant.DEFAULT_SLOW_INVOKE));
    }
}
