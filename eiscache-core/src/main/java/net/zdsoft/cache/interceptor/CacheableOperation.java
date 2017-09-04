package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.annotation.CacheOperation;
import net.zdsoft.cache.annotation.TimeToLive;

import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class CacheableOperation extends CacheOperation {

    private TimeUnit timeUnit;
    private int expire;
    private TimeToLive timeToLive;
    private String unless;

    public CacheableOperation(Builder builder) {
        super(builder);
        this.timeToLive = builder.timeToLive;
        this.expire = builder.expire;
        this.timeUnit = builder.timeUnit;
        this.unless = builder.unless;
    }

    public static class Builder extends CacheOperation.Builder {
        private TimeUnit timeUnit;
        private int expire;
        private TimeToLive timeToLive;
        private String unless;

        public Builder setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder setExpire(int expire) {
            this.expire = expire;
            return this;
        }

        public Builder setTimeToLive(TimeToLive timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public Builder setUnless(String unless) {
            this.unless = unless;
            return this;
        }
    }
}
