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

import net.zdsoft.cache.core.KeyGenerator;
import net.zdsoft.cache.expiry.ExpiryPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 17-9-3下午7:30
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    /**
     * 用于区分属于哪个缓存
     * @see CacheDefault
     */
    String cacheName() default "";

    /**
     * 缓存的key的生成策略，支持spring EL表达式 若为空将使用默认key生成策略 <br>
     */
    String key() default "";

    /**
     * 配合expire一起使用指定时间单位
     * @see Cacheable#expire()
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * {@code Cacheable#expire()} > global expiry config <br>
     * 过期时间，默认是0 单位是秒 永不过期，可配合 {@code Cacheable#timeUnit()} 一起使用 <br>
     * @see TimeUnit
     * @see ExpiryPolicy#getCreateExpire()
     * @see net.zdsoft.cache.expiry.Duration
     */
    int expire() default 0;

    /**
     * 最大存活时间，无论这个缓存在此期间有没有被访问
     * 单位秒
     */
    TimeToLive timeToLive() default TimeToLive.THIRTY_MINUTES;

    /**
     * 缓存条件支持spring EL 表达式 ，如果表达式结果为false将不会缓存<br>
     * ex:
     * <ul>
     *     <li>{@code #root.args[0]} {@code #root.target} {@code #root.method}</li>
     *     <li>{@code #root.result} 如果该方法的返回类型不是 {@link Void}，否则将会抛出异常</li>
     * </ul>
     *
     * 默认是空，总是缓存
     */
    String condition() default "";

    /**
     * 该方法没有具体实现
     * @deprecated 无用字段
     */
    String unless() default "";

    /**
     * 指定缓存对象的EntityId，支持spring EL <br>
     * <ul>
     *     <li>{@code #root.args[0]}</li>
     *     <li>{@code #result.![#this.id]}</li>
     * </ul>
     */
    String entityId() default "";

    /**
     * 当key通过spring EL 无法满足要求时，可使用该字段指定key发生器<br>
     * @see KeyGenerator
     */
    String keyGenerator() default "";
}
