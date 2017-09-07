package net.zdsoft.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author shenke
 * @since 2017.09.07
 */
public class RedisCacheManager extends AbstractCacheManager {

    private RedisTemplate redisTemplate;
    private static final String PREFIX_REDIS = "eis.v7";
    private String redisCachePrefix;

    public RedisCacheManager(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = super.getCache(cacheName);
        if ( cache == null ) {
            return createAndAdd(cacheName);
        }
        return cache;
    }

    private Cache createAndAdd(String cacheName) {
        RedisCache cache = new RedisCache(redisTemplate, cacheName, cacheName, PREFIX_REDIS);
        addCache(cache);
        return cache;
    }
}
