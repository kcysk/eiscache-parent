package net.zdsoft.cache.annotation;

import net.zdsoft.cache.expiry.ExpiryPolicy;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 17-9-3下午7:30
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    String cacheName() default "";

    /**
     * 缓存的key的生成策略，支持spring EL表达式 若为空将使用默认key生成策略 <br>
     */
    String key() default "";

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
     *
     */
    String unless() default "";
}
