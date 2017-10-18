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

    private Object[] key;
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

    public CacheEvent setKey(Object[] key) {
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
