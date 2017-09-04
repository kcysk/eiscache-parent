package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;

import java.util.Set;

/**
 * @author shenke
 * @since 17-9-3下午11:26
 */
public interface Cache {

    <K, V> V get(K key, Class<V> type);

    <K, V> void put(K key, V value);

    <K> void remove(K key);

    <K> void removeAll(Set<K> keys);

    void removeAll();

    <C extends CacheConfiguration> C getConfiguration();

    interface Entry {

        String getKey();

        String getValue();

        long getCreateTime();
    }
}
