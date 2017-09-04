package net.zdsoft.cache.event;

import net.zdsoft.cache.Cache;
import net.zdsoft.cache.expiry.Duration;

/**
 * @author shenke
 * @since 17-9-4下午10:22
 */
public class CacheableEvent extends CacheEvent {

    private Duration duration;
    private long createTime;

    public CacheableEvent(EventType source, Cache cache) {
        super(source, cache);
    }

    @Override
    public <V> V getOldValue() {
        return null;
    }

    @Override
    public <K> K getKey() {
        return null;
    }
}
