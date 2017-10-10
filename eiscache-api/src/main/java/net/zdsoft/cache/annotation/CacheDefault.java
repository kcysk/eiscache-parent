package net.zdsoft.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口优先原则，优先使用{@link Cacheable}中的cacheName 支持spring EL<br>
 * #getFirstGenericType(#root.targetClass).newInstance().fetchCacheEntitName()
 * @author shenke
 * @since 2017.09.04
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheDefault {

    String cacheName() default "";

    String keyGenerator() default "";
}
