package net.zdsoft.cache.listener;

import net.zdsoft.cache.event.CacheEvent;

import java.util.EventListener;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface CacheEventListener extends EventListener {

    void doInvoke(CacheEvent cacheEvent);
}
