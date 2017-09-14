package net.zdsoft.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**#getFirstGenericType(#root.targetClass).newInstance().fetchCacheEntitName()
 * @author shenke
 * @since 2017.09.04
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDefault {

    String cacheName() default "";
}
