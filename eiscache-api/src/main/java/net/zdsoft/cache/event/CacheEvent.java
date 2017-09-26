package net.zdsoft.cache.event;

import net.zdsoft.cache.core.Cache;

import java.util.EventObject;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class CacheEvent extends EventObject {

    private Cache cache;
    private EventType eventType;

    private Object key;
    private Object value;
    private long duration;

    public CacheEvent(Cache source, EventType eventType) {
        super(source);
        this.cache = source;
        this.eventType = eventType;
    }

    @Override
    public Cache getSource() {
        return (Cache) super.getSource();
    }

    public final EventType getEventType() {
        return eventType;
    }

    public Object getKey() {
        return key;
    }

    public CacheEvent setKey(Object key) {
        this.key = key;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CacheEvent setValue(Object value) {
        this.value = value;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public CacheEvent setDuration(long duration) {
        this.duration = duration;
        return this;
    }
}
