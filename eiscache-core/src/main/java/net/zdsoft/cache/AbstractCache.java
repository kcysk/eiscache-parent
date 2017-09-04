package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;
import net.zdsoft.cache.event.CacheEvent;
import net.zdsoft.cache.event.CacheRemoveListener;
import net.zdsoft.cache.event.EventType;

import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class AbstractCache implements Cache{

    private CacheConfiguration cacheConfiguration;

    public AbstractCache(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    public AbstractCache() {

    }

    @Override
    public <C extends CacheConfiguration> C getConfiguration() {
        return (C) this.cacheConfiguration;
    }

    @Override
    public <K, V> V get(K key, Class<V> type) {
        return doGet(key, type);
    }

    protected abstract  <K, V> V doGet(K key, Class<V> type);

    @Override
    public <K> void remove(K key) {

        final Object oldValue = get(key, null);
        CacheEvent removeEvent = new CacheEvent(EventType.REMOVE, this) {
            @Override
            public <V> V getOldValue() {
                return (V) oldValue;
            }

            @Override
            public <K> K getKey() {
                return (K) key;
            }
        };
        CacheRemoveListener cacheRemoveListener = (CacheRemoveListener) getConfiguration().getListener(CacheRemoveListener.class);

        try {
            doRemove(key);
        } catch (Exception e){
            cacheRemoveListener.onRemove(removeEvent, e);
        }
        cacheRemoveListener.onRemove(removeEvent);
    }

    protected abstract <K> void doRemove(K key);

    @Override
    public <K, V> void put(K key, V value) {

        doPut(key, value);
    }

    protected abstract <K, V> void doPut(K key, V value);

    @Override
    public <K> void removeAll(Set<K> keys) {
        doRemoveAll(keys);
    }

    protected abstract <K> void doRemoveAll(Set<K> keys);

    @Override
    public void removeAll() {
        doRemoveAll();
    }

    protected abstract void doRemoveAll();
}
