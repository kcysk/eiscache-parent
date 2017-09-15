package net.zdsoft.cache.core.support;

import net.zdsoft.cache.core.CacheOperation;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class CacheRemoveOperation extends CacheOperation {

    private boolean afterInvocation;
    private boolean allEntries;

    public CacheRemoveOperation(Builder builder) {
        super(builder);
        this.afterInvocation = builder.afterInvocation;
        this.allEntries = builder.allEntries;
    }

    public boolean isAfterInvocation() {
        return afterInvocation;
    }

    public static class Builder extends CacheOperation.Builder {

        private boolean afterInvocation;
        private boolean allEntries;

        public Builder setAfterInvocation(boolean afterInvocation) {
            this.afterInvocation = afterInvocation;
            return this;
        }

        public Builder setAllEntries(boolean allEntries) {
            this.allEntries = allEntries;
            return this;
        }
    }

    public boolean isAllEntries() {
        return allEntries;
    }
}
