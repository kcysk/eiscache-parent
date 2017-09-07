package net.zdsoft.cache;

import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke
 * @since 2017.09.07
 */
public abstract class AbstractCacheManager implements CacheManager , InitializingBean{

    private Map<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = cacheMap.get(cacheName);
        if ( cache  == null ) {
            return lookup(cacheName);
        }
        return cache;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        initCache();
    }

    @Override
    public void destroy(String cacheName) {
        if ( cacheMap.get(cacheName) != null ) {
            synchronized (cacheMap) {
                cacheMap.get(cacheName).destroy();
                cacheMap.remove(cacheName);
            }
        }
    }

    private void initCache() {
        Collection<Cache> caches = loadCache();
        if ( caches != null ) {
            synchronized (cacheMap) {
                for (Cache cache : caches) {
                    cacheMap.put(cache.getName(), cache);
                }
            }
        }
    }

    protected Collection<Cache> loadCache(){

        return null;
    }

    protected void addCache(Cache cache) {
        synchronized (cacheMap) {
            cacheMap.put(cache.getName(), cache);
        }
    }

    protected Cache lookup(String cacheName) {
        return null;
    }
}
