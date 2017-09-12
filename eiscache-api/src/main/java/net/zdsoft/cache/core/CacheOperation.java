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
