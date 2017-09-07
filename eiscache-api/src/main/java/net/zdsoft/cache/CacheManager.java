package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface CacheManager {

    //<C extends CacheConfiguration> Cache createCache(String name, C configuration);

    Cache getCache(String cacheName);

    void destroy(String cacheName);
}
