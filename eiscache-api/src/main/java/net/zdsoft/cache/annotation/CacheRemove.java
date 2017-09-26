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
