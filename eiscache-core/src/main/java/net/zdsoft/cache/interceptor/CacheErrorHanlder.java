package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Cache;

/**
 * @author shenke
 * @since 2017.09.06
 */
public interface CacheErrorHanlder {

    void doPutError(RuntimeException e, Cache cache, Object key, Object value);

    void doRemoveError(RuntimeException e, Cache cache, Object key);

    void doGetError(RuntimeException e, Cache cache, Object key);
}
