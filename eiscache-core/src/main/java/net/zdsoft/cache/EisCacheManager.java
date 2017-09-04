package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke
 * @since 17-9-4下午10:47
 */
public class EisCacheManager implements CacheManager {

    private Map<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    @Override
    public <C extends CacheConfiguration> Cache createCache(String name, C configuration) {

        return null;
    }

    @Override
    public Cache getCache(String cacheName) {
        return cacheMap.get(cacheName);
    }

    @Override
    public void destroy(String cacheName) {

    }
}
