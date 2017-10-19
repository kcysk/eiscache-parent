/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
