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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shenke
 * @since 17-9-3下午11:12
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRemove {

    String cacheName() default "";

    /**
     * 缓存的key的生成策略，支持spring EL表达式 <br>
     */
    String key() default "";

    /**
     * 是否在方法实际调用之后执行，默认false
     */
    boolean afterInvocation() default false;

    /**
     * 缓存条件支持spring EL<br>
     */
    String condition() default "";

    /**
     * 实体类ID可使用spring EL
     */
    String entityId() default "";

    /**
     * true, 指定缓存下面的所有缓存数据都被清空
     */
    boolean allEntries() default false;

    /**
     * key 发生器
     */
    String keyGenerator() default "";
}
