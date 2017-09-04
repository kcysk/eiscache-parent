package net.zdsoft.cache.event;

import net.zdsoft.cache.Cache;

import java.util.EventObject;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheEvent extends EventObject {

    private Cache cache;
    private EventType eventType;

    public CacheEvent(EventType source, Cache cache) {
        super(source);
        this.eventType = source;
        this.cache = cache;
    }

    @Override
    public Cache getSource() {
        return (Cache) super.getSource();
    }

    public abstract <V> V getOldValue();

    public abstract <K> K getKey();

    public final EventType getEventType() {
        return eventType;
    }
}
