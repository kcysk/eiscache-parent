package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.annotation.CacheOperation;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class CacheRemoveOperation extends CacheOperation {

    private boolean afterInvocation;

    public CacheRemoveOperation(Builder builder, boolean afterInvocation) {
        super(builder);
        this.afterInvocation = afterInvocation;
    }

    public boolean isAfterInvocation() {
        return afterInvocation;
    }
}
