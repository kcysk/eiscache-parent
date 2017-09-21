package net.zdsoft.cache;

import net.zdsoft.cache.configuration.ByteTransfer;
import net.zdsoft.cache.configuration.CacheConfiguration;
import net.zdsoft.cache.configuration.ValueTransfer;
import net.zdsoft.cache.expiry.Duration;
import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.expiry.TTLExpiryPolicy;
import net.zdsoft.cache.listener.CacheEventListener;
import net.zdsoft.cache.support.DefaultByteTransfer;
import net.zdsoft.cache.support.JSONValueTransfer;
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
    private String redisCachePrefix;
    private static Map<String, String> CACHE_GLOBAL_PREFIX = new ConcurrentHashMap<String, String>();
    private static final ValueTransfer VALUE_TRANSFER = new JSONValueTransfer();
    private static final ByteTransfer BYTETR_TANSFER = new DefaultByteTransfer();

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
        CACHE_GLOBAL_PREFIX.put(cacheName, PREFIX_REDIS + "." + cacheName +".");
        RedisCache cache = new RedisCache(redisTemplate, cacheName, PREFIX_REDIS, BYTETR_TANSFER, VALUE_TRANSFER);
        cache.setCacheConfiguration(getDefaultConfiguration());
        addCache(cache);
        return cache;
    }

    private CacheConfiguration<String> getDefaultConfiguration() {

        return new Configuration();
    }

    @Override
    public String getGlobalPrefix(String cacheName) {
        return null;
    }

    class Configuration implements CacheConfiguration{
        ExpiryPolicy expiry = new TTLExpiryPolicy(Duration.NEVER);

        @Override
        public CacheEventListener getListener(Class listenerClass) {
            return null;
        }

        @Override
        public ExpiryPolicy getExpiry() {
            return null;
        }

        @Override
        public ValueTransfer getValueTransfer() {
            return null;
        }

        @Override
        public Class<String> getKeyType() {
            return String.class;
        }
    }
}
