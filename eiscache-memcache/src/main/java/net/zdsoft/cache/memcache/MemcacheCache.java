package net.zdsoft.cache.memcache;

import net.zdsoft.cache.configuration.Configuration;
import net.zdsoft.cache.core.Cache;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 2017.09.26
 */
public class MemcacheCache implements Cache {

    private

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public Object getNative(Object key) {
        return null;
    }

    @Override
    public CacheWrapper get(Object key) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Set<String> entityId, Object key, Object value) {

    }

    @Override
    public void put(Set<String> entityId, Object key, Object value, long seconds) {

    }

    @Override
    public void put(Set<String> entityId, Object key, Object value, int account, TimeUnit timeUnit) {

    }

    @Override
    public CacheWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public void remove(Set<String> entityId, Object key) {

    }

    @Override
    public void remove(Set<String> entityId, Object... keys) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public long incrBy(Object key, int value) {
        return 0;
    }
}
