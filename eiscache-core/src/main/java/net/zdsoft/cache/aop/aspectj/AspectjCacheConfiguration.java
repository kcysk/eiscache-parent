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
package net.zdsoft.cache.aop.aspectj;

import net.zdsoft.cache.aop.AbstractCacheConfiguration;
import net.zdsoft.cache.aop.CacheBeanFactoryPointCutAdvisor;
import net.zdsoft.cache.aop.DynamicCacheClassFilter;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * @author shenke
 * @since 2017.08.30
 */
public class AspectjCacheConfiguration extends AbstractCacheConfiguration {

    @Bean(name = "net.zdsoft.cache.aspect.exCacheAspectj")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheAspectj cacheAspect(TypeDescriptor typeDescriptor,
                                    CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        CacheAspectj aspectj = new CacheAspectj();
        CacheBeanFactoryPointCutAdvisor advisor = (CacheBeanFactoryPointCutAdvisor) cacheAdvisor(null, cacheOperationParser, filter);
        aspectj.setAdvisor(advisor);
        aspectj.setTypeDescriptor(typeDescriptor);
        aspectj.setAdvisor(advisor);
        setSlowTime(aspectj);
        return aspectj;
    }

}
