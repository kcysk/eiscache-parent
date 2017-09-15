package net.zdsoft.cache.annotation;

import java.lang.annotation.*;

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
     * 缓存的key的生成策略，支持spring EL表达式 若为空将使用默认key生成策略 <br>
     */
    String key() default "";

    /**
     * 是否在方法实际调用之后执行，默认false
     */
    boolean afterInvocation() default false;

    String condition() default "";

    String entityId() default "";

    boolean allEntries() default false;
}
