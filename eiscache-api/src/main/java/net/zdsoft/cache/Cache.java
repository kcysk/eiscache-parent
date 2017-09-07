package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;
import org.springframework.cache.CacheManager;

import java.util.Set;
import java.util.concurrent.Callable;

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

    void put(Object key, Object value);

    Object putIfAbsent(Object key, Object value, Class<?> type);

    <C extends CacheConfiguration> C getConfiguration();

    void remove(Object key);

    void remove(Object... keys);

    void destroy();
}
