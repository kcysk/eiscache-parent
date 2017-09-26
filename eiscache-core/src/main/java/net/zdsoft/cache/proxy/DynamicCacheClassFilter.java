package net.zdsoft.cache.proxy;

/**
 * @author shenke
 * @since 2017.09.21
 */
public interface DynamicCacheClassFilter {

    boolean matches(Class<?> var1);
}
