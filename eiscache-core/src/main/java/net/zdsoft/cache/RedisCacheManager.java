package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;
import net.zdsoft.cache.configuration.ValueTransfer;
import net.zdsoft.cache.expiry.Duration;
import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.expiry.TTLExpiryPolicy;
import net.zdsoft.cache.listener.CacheEventListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author shenke
 * @since 2017.09.07
 */
public class RedisCacheManager extends AbstractCacheManager {

    private RedisTemplate redisTemplate;
    private static final String PREFIX_REDIS = "cache.";
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
        cache.setCacheConfiguration(getDefaultConfiguration());
        addCache(cache);
        return cache;
    }

    private CacheConfiguration<String> getDefaultConfiguration() {

        return new Configuration();
    }

    class Configuration implements CacheConfiguration<String> {
        ExpiryPolicy expiry = new TTLExpiryPolicy(Duration.NEVER);
        @Override
        public <L extends CacheEventListener> L getListener(Class<L> listenerClass) {
            return null;
        }

        @Override
        public <E extends ExpiryPolicy> E getExpiry() {
            return (E) expiry;
        }

        @Override
        public <S, T> ValueTransfer<S, T> getValueTransfer() {
            return null;
        }

        @Override
        public Class<String> getKeyType() {
            return String.class;
        }
    }
}
