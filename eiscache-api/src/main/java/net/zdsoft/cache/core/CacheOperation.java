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
package net.zdsoft.cache.core;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheOperation {

    private String cacheName;
    private String key;
    private String condition;
    private String entityId;

    public CacheOperation(Builder builder) {
        this.cacheName = builder.cacheName;
        this.key = builder.key;
        this.condition = builder.condition;
        this.entityId = builder.entityId;
    }

    public String getCacheName() {
        return cacheName;
    }

    public String getKey() {
        return key;
    }

    public String getCondition() {
        return condition;
    }

    public String getEntityId() {
        return entityId;
    }

    public abstract static class Builder {

        private String cacheName;
        private String key;
        private String condition;
        private String entityId;

        public Builder setCacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder setEntityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public String getCacheName() {
            return cacheName;
        }

        public static Builder builder() {
            return new Builder() {};
        }
    }
}
