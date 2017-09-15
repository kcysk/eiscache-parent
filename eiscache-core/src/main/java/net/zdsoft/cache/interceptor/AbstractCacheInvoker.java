package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Cache;
import net.zdsoft.cache.event.CacheEvent;
import net.zdsoft.cache.event.EventType;
import net.zdsoft.cache.listener.CacheEventListener;
import net.zdsoft.cache.listener.CacheRemoveListener;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 2017.09.06
 */
public abstract class AbstractCacheInvoker {

    protected abstract CacheErrorHanlder getCacheErrorHandler();

    protected abstract Collection<CacheEventListener> getCacheEventListener();

    protected void doPut(Set<String> entityId, Cache cache, Object key, Object value) {
        try {
            //CacheEvent cachePutEvent = new CacheableEvent(EventType.CREATE, );
            cache.put(entityId, key, value);
        } catch (RuntimeException e) {
            getCacheErrorHandler().doPutError(e, cache, key, value);
        }
    }

    protected void doPut(Set<String> entityId,Cache cache, Object key, Object value, int account, TimeUnit timeUnit) {
        try {
            cache.put(entityId, key, value, account, timeUnit);
        } catch (RuntimeException e) {
            getCacheErrorHandler().doPutError(e, cache, key, value);
        }
    }

    protected Object doGet(Cache cache, Object key, Class<?> returnType) {
        try {
            return cache.get(key, returnType);
        } catch (RuntimeException e){
            e.printStackTrace();
            getCacheErrorHandler().doGetError(e, cache, key);
        }
        return null;
    }

    protected void doRemove(final Cache cache, final Object key, Set<String> entityId) {
        try {
            CacheEvent cacheEvent = new CacheEvent(cache, EventType.REMOVE);
            cacheEvent.setKey(key);
            notifyListener(cacheEvent);
            cache.remove(entityId, key);  //FIXME 可改为异步处理
        } catch (RuntimeException e){
            getCacheErrorHandler().doRemoveError(e, cache, key);
        }
    }

    protected void doRemoveAll(Cache cache) {
        try {
            cache.removeAll();
        } catch (Exception e){

        }
    }

    protected void doRemove(final Cache cache, Object[] keys, Set<String> entityId) {
        try {
            cache.remove(entityId, keys);
        } catch (RuntimeException e) {

        }
    }

    private void notifyListener(CacheEvent cacheEvent) {
        for (CacheEventListener listener : getCacheEventListener()) {
            if ( EventType.REMOVE.equals(cacheEvent.getEventType()) ) {
                if ( listener instanceof CacheRemoveListener ) {
                    listener.doInvoke(cacheEvent);
                }
            }
        }
    }
}
