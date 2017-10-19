/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
