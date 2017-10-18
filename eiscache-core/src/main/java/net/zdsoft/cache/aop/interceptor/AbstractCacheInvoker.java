/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.zdsoft.cache.aop.interceptor;

import net.zdsoft.cache.core.Cache;
import net.zdsoft.cache.event.CacheEvent;
import net.zdsoft.cache.event.EventType;
import net.zdsoft.cache.expiry.Duration;
import net.zdsoft.cache.listener.CacheEventListener;
import net.zdsoft.cache.listener.CacheRemoveListener;
import net.zdsoft.cache.support.ReturnTypeContext;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 2017.09.06
 */
public abstract class AbstractCacheInvoker {

    private Logger logger = Logger.getLogger(AbstractCacheInvoker.class);

    protected ThreadLocal<MethodInvocation> invocationContext = new ThreadLocal<MethodInvocation>();

    protected abstract CacheErrorHandler getCacheErrorHandler();

    protected abstract Collection<CacheEventListener> getCacheEventListener();

    protected void doPut(Set<String> entityId, Cache cache, Object key, Object value) {
        doPut(entityId, cache, key, value, 0, null);
    }

    protected void doPut(Set<String> entityId,Cache cache, Object key, Object value, int account, TimeUnit timeUnit) {
        try {
            cache.put(entityId, key, value, account, timeUnit);
        } catch (RuntimeException e) {
            getCacheErrorHandler().doPutError(e, cache, key, value);
        }
    }

    protected Object doGet(Cache cache, Object key) {
        try {
            Type type = ReturnTypeContext.getReturnType();
            return cache.get(key).get(type);
        } catch (RuntimeException e){
            getCacheErrorHandler().doGetError(e, cache, key);
        }
        return null;
    }

    protected void doRemove(final Cache cache, final Object key, Set<String> entityId) {
        try {
            CacheEvent cacheEvent = new CacheEvent(cache, EventType.REMOVE);
            cacheEvent.setKey(new Object[]{key});
            notifyListener(cacheEvent);
            cache.remove(entityId, key);  //FIXME 可改为异步处理
        } catch (RuntimeException e){
            getCacheErrorHandler().doRemoveError(e, cache, key);
        }
    }

    protected void doRemoveAll(Cache cache) {
        try {
            cache.removeAll();
        } catch (RuntimeException e){
            getCacheErrorHandler().doRemoveError(e, cache);
        }
    }

    protected void doRemove(final Cache cache, Object[] keys, Set<String> entityId) {
        try {
            cache.remove(entityId, keys);
        } catch (RuntimeException e) {
            getCacheErrorHandler().doRemoveError(e, cache, keys);
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
