package net.zdsoft.cache.aop;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.aop.interceptor.CacheAopExecutor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import net.zdsoft.cache.aop.proxy.CacheInterceptor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;

/**
 * @author shenke
 * @since 2017.10.11
 */
public abstract class AbstractCacheConfiguration implements EnvironmentAware {

    protected Environment environment;

    @Bean(name = Constant.BEAN_NAME_ADVISOR)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor, CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        AbstractBeanFactoryPointcutAdvisor advisor = new CacheBeanFactoryPointCutAdvisor();
        ((CacheBeanFactoryPointCutAdvisor)advisor).setCacheOperationParser(cacheOperationParser);
        advisor.setAdvice(cacheInterceptor);
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
        aopExecutor.setSlowInvokeTime(NumberUtils.toLong(slowInvoke, Constant.DEFAULT_SLOW_CACHE));
    }
}