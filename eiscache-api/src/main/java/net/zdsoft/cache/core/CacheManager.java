package net.zdsoft.cache.core;

import net.zdsoft.cache.configuration.Configuration;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface CacheManager {

    Cache getCache(String cacheName);

    Cache getCache(String cacheName, Configuration configuration);

    void destroy(String cacheName);

    String getGlobalPrefix(String cacheName);
}
