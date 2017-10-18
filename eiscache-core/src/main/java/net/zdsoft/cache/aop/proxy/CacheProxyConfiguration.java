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
package net.zdsoft.cache.aop.proxy;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.aop.AbstractCacheConfiguration;
import net.zdsoft.cache.aop.DynamicCacheClassFilter;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.09.06
 */
public class CacheProxyConfiguration extends AbstractCacheConfiguration {

    @Bean
    public CacheInterceptor cacheInterceptor(TypeDescriptor typeDescriptor, CacheOperationParser parser){
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        cacheInterceptor.setTypeDescriptor(typeDescriptor);
        cacheInterceptor.setCacheOperationParser(parser);
        setSlowTime(cacheInterceptor);
        return cacheInterceptor;
    }

    @Bean(name = Constant.BEAN_NAME_ADVISOR)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Override
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor, CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        return super.cacheAdvisor(cacheInterceptor, cacheOperationParser, filter);
    }
}
