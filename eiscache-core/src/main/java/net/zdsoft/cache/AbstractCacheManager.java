package net.zdsoft.cache;

import net.zdsoft.cache.configuration.Configuration;
import net.zdsoft.cache.core.Cache;
import net.zdsoft.cache.core.CacheManager;
import net.zdsoft.cache.support.GlobalConfiguration;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke
 * @since 2017.09.07
 */
public abstract class AbstractCacheManager implements CacheManager, InitializingBean{

    private Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
    private Configuration defaultConfiguration = new GlobalConfiguration();
    private Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = cacheMap.get(cacheName);
        if ( cache  == null ) {
            return lookup(cacheName);
        }
        return cache;
    }

    @Override
    public String getGlobalPrefix(String cacheName) {
        return null;
    }

    @Override
    public Cache getCache(String cacheName, Configuration configuration) {
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

    protected Configuration getConfiguration() {
        return this.configuration == null ? this.defaultConfiguration : this.configuration;
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

    private Collection<Cache> loadCache(){

        return null;
    }

    protected void addCache(Cache cache) {
        synchronized (cacheMap) {
            cacheMap.put(cache.getName(), cache);
        }
    }

    private Cache lookup(String cacheName) {
        return null;
    }
}
