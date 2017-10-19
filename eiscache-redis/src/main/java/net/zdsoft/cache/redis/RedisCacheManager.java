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
package net.zdsoft.cache.redis;

import net.zdsoft.cache.AbstractCacheManager;
import net.zdsoft.cache.configuration.Configuration;
import net.zdsoft.cache.core.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke
 * @since 2017.09.07
 */
public class RedisCacheManager extends AbstractCacheManager {

    private RedisTemplate redisTemplate;
    private static final String PREFIX_REDIS = "cache";
    private static Map<String, String> CACHE_GLOBAL_PREFIX;

    public RedisCacheManager(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        CACHE_GLOBAL_PREFIX = new ConcurrentHashMap<String, String>();
    }

    @Override
    public Cache getCache(String cacheName) {
        return getCache(cacheName, getConfiguration());
    }

    @Override
    public Cache getCache(String cacheName, Configuration configuration) {
        Cache cache = super.getCache(cacheName);
        if ( cache == null ) {
            return createAndAdd(cacheName, configuration);
        }
        return cache;
    }

    private Cache createAndAdd(String cacheName, Configuration configuration) {
        CACHE_GLOBAL_PREFIX.put(cacheName, PREFIX_REDIS + "." + cacheName +".");
        RedisCache cache = new RedisCache(redisTemplate, cacheName, PREFIX_REDIS, getConfiguration());
        addCache(cache);
        return cache;
    }

    @Override
    public String getGlobalPrefix(String cacheName) {
        return null;
    }

}
