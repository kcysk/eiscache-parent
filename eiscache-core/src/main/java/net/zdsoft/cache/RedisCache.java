package net.zdsoft.cache;

import com.alibaba.fastjson.JSON;
import net.zdsoft.cache.configuration.CacheConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class RedisCache implements Cache{

    private CacheConfiguration cacheConfiguration;
    private String name;
    private String prefix;
    private String cachePrefix;
    private RedisTemplate redisTemplate;

    private byte[] keySetName;


    public RedisCache(RedisTemplate redisTemplate, String name, String prefix, String cachePrefix) {
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.prefix = prefix;
        this.cachePrefix = cachePrefix;
        this.keySetName = redisTemplate.getKeySerializer().serialize(name + "<~>keys");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object value = redisTemplate.execute(new RedisCallback<T>() {
            @Override
            public T doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                byte[] value = connection.get(keyBytes);
                if ( native_type.contains(type) ) {
                    return (T) redisTemplate.getValueSerializer().deserialize(value);
                }
                return JSON.parseObject(new String(value), type);
            }
        }, true);
        return (T) value;
    }

    @Override
    public void remove(Object key) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);

                connection.del(keyBytes);
                connection.zRem(keyBytes);
                return null;
            }
        },true);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.set(keyBytes, redisTemplate.getValueSerializer().serialize(value));
                connection.zAdd(keySetName, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(Object key, Object value, long seconds) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.setEx(keyBytes, seconds, redisTemplate.getValueSerializer().serialize(value));
                connection.zAdd(keySetName, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(Object key, Object value, int account, TimeUnit timeUnit) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                if ( TimeUnit.MICROSECONDS.equals(timeUnit) ) {
                    connection.pSetEx(keyBytes, timeUnit.toMillis(account), redisTemplate.getValueSerializer().serialize(value));
                } else {
                  connection.setEx(keyBytes, timeUnit.toSeconds(account), redisTemplate.getValueSerializer().serialize(value));
                }
                connection.zAdd(keySetName, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public Object get(Object key) {

        return null;
    }

    @Override
    public Object putIfAbsent(Object key, Object value, Class<?> type) {
        return null;
    }

    @Override
    public <C extends CacheConfiguration> C getConfiguration() {
        return (C) this.cacheConfiguration;
    }

    @Override
    public void remove(Object... keys) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                Collection<byte[]> keyCollections = Collections.EMPTY_SET;
                for (Object key : keys) {
                    keyCollections.add(getKey(key));
                }
                connection.del(keyCollections.toArray(new byte[keyCollections.size()][]));
                connection.zRem(keySetName, keyCollections.toArray(new byte[keyCollections.size()][]));
                return null;
            }
        });
    }

    @Override
    public void destroy() {

    }

    private byte[] getKey(Object key) {
        byte[] keyBytes = null;
        if ( getConfiguration().getKeyType().equals(String.class) ) {
            keyBytes = convertToByteIfNecessary(cachePrefix + prefix + key.toString() , redisTemplate.getKeySerializer());
        }
        if ( keyBytes == null ) {
            keyBytes = convertToByteIfNecessary(key, redisTemplate.getKeySerializer());
        }
        return keyBytes;
    }

    private byte[] convertToByteIfNecessary (Object key, RedisSerializer serializer) {
        if ( key != null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return serializer.serialize(key);
    }

    private static final Set<Class<?>> native_type = new HashSet<Class<?>>(16){{
        add(int.class);
        add(Integer.class);
        add(float.class);
        add(Float.class);
        add(char.class);
        add(Character.class);
        add(double.class);
        add(Double.class);
        add(String.class);
        add(boolean.class);
        add(Boolean.class);
    }};

    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }
}
