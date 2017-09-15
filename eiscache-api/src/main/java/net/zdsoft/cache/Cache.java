package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;
import org.springframework.cache.CacheManager;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 17-9-3下午11:26
 */
public interface Cache {

    String getName();

    Object getNativeCache();

    Object get(Object key);

    <T> T get(Object key, Class<T> type);

    <T> T get(Object key, Callable<T> valueLoader);

    void put(Set<String> entityId, Object key, Object value);

    void put(Set<String> entityId, Object key, Object value, long seconds);

    void put(Set<String> entityId, Object key, Object value, int account, TimeUnit timeUnit);

    Object putIfAbsent(Object key, Object value, Class<?> type);

    <C extends CacheConfiguration> C getConfiguration();

    void remove(Set<String> entityId, Object key);

    void remove(Set<String> entityId, Object... keys);

    void removeAll();

    void destroy();
}
