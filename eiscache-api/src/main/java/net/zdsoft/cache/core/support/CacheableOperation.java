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
package net.zdsoft.cache.core.support;

import net.zdsoft.cache.core.CacheOperation;
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

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getExpire() {
        return expire;
    }

    public TimeToLive getTimeToLive() {
        return timeToLive;
    }

    public String getUnless() {
        return unless;
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
