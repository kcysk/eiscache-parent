package net.zdsoft.cache.aop;

/**
 * @author shenke
 * @since 2017.09.21
 */
public interface DynamicCacheClassFilter {

    boolean matches(Class<?> var1);
}
