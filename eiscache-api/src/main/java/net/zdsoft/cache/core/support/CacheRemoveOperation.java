package net.zdsoft.cache.core.support;

import net.zdsoft.cache.core.CacheOperation;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class CacheRemoveOperation extends CacheOperation {

    private boolean afterInvocation;

    public CacheRemoveOperation(Builder builder) {
        super(builder);
        this.afterInvocation = builder.afterInvocation;
    }

    public boolean isAfterInvocation() {
        return afterInvocation;
    }

    public static class Builder extends CacheOperation.Builder {

        private boolean afterInvocation;

        public Builder setAfterInvocation(boolean afterInvocation) {
            this.afterInvocation = afterInvocation;
            return this;
        }
    }
}
