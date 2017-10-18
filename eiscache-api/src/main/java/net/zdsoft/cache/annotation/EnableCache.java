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
package net.zdsoft.cache.annotation;

import net.zdsoft.cache.integration.spring.Advice;
import net.zdsoft.cache.integration.spring.CacheConfigurationSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring 版本低于3.1 请不要使用 参见
 * {@code net.zdsoft.cache.aop.aspectj.CacheAspectj} <br>
 * @author shenke
 * @since 2017.09.04
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheConfigurationSelector.class)
public @interface EnableCache {

    Advice advice() default Advice.NONE;

    int order() default Ordered.LOWEST_PRECEDENCE;
}
