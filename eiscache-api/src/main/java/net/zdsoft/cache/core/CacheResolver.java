package net.zdsoft.cache.core;

import net.zdsoft.cache.Cache;

/**
 * @author shenke
 * @since 17-9-4下午10:20
 */
public interface CacheResolver {

    Cache resolver(CacheInvocationContext cacheInvocationContext);

}
