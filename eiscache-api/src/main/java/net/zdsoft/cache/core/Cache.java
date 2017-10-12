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

    /**
     * 获取缓存结果包装对象
     * @see CacheWrapper
     * @param key key
     * @return
     */
    CacheWrapper get(Object key);

    /**
     *
     * @param key
     * @param valueLoader
     * @param <T>
     * @return
     */
    <T> T get(Object key, Callable<T> valueLoader);

    /**
     * 数据放入缓存，会将entityId作为Key， key作为value放入缓存以备更新数据用 <br>
     * entityId 作为key会做如下拼接 (prefix 通用和自定义前缀) prefix + ".id-key." + entityId <br>
     * 此外key会作为value值存放到当前缓存的key缓存中（即只是用来保存当前缓存key的缓存）<br>
     * @param entityId  实体类主键 可为空
     * @param key       唯一key （并不是最终的key，仍会再次拼接）
     * @param value     待缓存数据
     */
    void put(Set<String> entityId, Object key, Object value);

    /**
     * 数据放入缓存
     * @see   this#put(Set, Object, Object)
     * @param entityId  实体类主键 可为空
     * @param key       key
     * @param value     待缓存数据
     * @param seconds   缓存时间 单位：s
     */
    void put(Set<String> entityId, Object key, Object value, long seconds);

    /**
     * 精度相对来说比 {@link this#put(Set, Object, Object, long)} 要高， 取决于具体的缓存实现
     * @see   this#put(Set, Object, Object, long)
     * @param entityId  实体类主键
     * @param key       key
     * @param value     待缓存数据
     * @param account   缓存时间数量
     * @param timeUnit  缓存时间单位 （最终转换为ms）
     */
    void put(Set<String> entityId, Object key, Object value, int account, TimeUnit timeUnit);

    CacheWrapper putIfAbsent(Object key, Object value);

    Configuration getConfiguration();

    void remove(Set<String> entityId, Object key);

    void remove(Set<String> entityId, Object... keys);

    void removeAll();

    void destroy();

    long incrBy(Object key, int value);

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
    }
}
