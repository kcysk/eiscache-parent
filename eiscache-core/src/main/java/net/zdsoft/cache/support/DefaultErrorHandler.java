package net.zdsoft.cache.support;

import net.zdsoft.cache.core.Cache;
import net.zdsoft.cache.aop.interceptor.CacheErrorHanlder;

/**
 * @author shenke
 * @since 2017.09.07
 */
public class DefaultErrorHandler implements CacheErrorHanlder {

    @Override
    public void doPutError(RuntimeException e, Cache cache, Object key, Object value) {
        throw e;
    }

    @Override
    public void doRemoveError(RuntimeException e, Cache cache, Object ... key) {
        throw e;
    }

    @Override
    public void doGetError(RuntimeException e, Cache cache, Object key) {
        throw e;
    }
}
