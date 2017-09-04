package net.zdsoft.cache.listener;

import net.zdsoft.cache.event.CacheEvent;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface CacheRemoveListener extends CacheEventListener {

    void onRemove(CacheEvent cacheEvent);

    void onRemove(CacheEvent cacheEvent, Throwable throwable);
}
