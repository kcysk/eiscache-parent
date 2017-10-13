package net.zdsoft.cache.core;

import net.zdsoft.cache.configuration.Configuration;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 17-9-3下午11:26
 */
public interface Cache {

    String getName();

    /**
     * maybe Jedis or memecache or redisTemplate
     */
    Object getNativeCache();

    /**
     * 原始缓存对象，String
     * @param key
     * @return
     */
    Object getNative(Object key);

    CacheWrapper get(Object key);

    <T> T get(Object key, Callable<T> valueLoader);

    void put(Set<String> entityId, Object key, Object value);

    void put(Set<String> entityId, Object key, Object value, long seconds);

    void put(Set<String> entityId, Object key, Object value, int account, TimeUnit timeUnit);

    CacheWrapper putIfAbsent(Object key, Object value);

    Configuration getConfiguration();

    void remove(Set<String> entityId, Object key);

    void remove(Set<String> entityId, Object... keys);

    void removeAll();

    void destroy();

    long incrBy(Object key, long value);

    /**
     * 复杂Map，List请自行转换
     */
    interface CacheWrapper {

        <T> T getEntity(Class<T> tClass);

        <K,V> Map<K,V> getMap(Type kType, Type vType);

        <K,V> Map<K,V> getMap(Type genericType);

        <T> List<T> getList(Type tType);

        <T> Set<T> getSet(Type type);

        <T> T get(Type genericType);

        String getNative();
    }
}
