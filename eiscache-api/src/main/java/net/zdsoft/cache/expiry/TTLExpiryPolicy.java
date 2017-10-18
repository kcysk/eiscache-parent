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
package net.zdsoft.cache.expiry;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class TTLExpiryPolicy implements ExpiryPolicy {

    private Duration duration;

    public TTLExpiryPolicy(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Duration getAccessExpire() {
        return this.duration;
    }

    @Override
    public Duration getUpdateExpire() {
        return this.duration;
    }

    @Override
    public Duration getCreateExpire() {
        return this.duration;
    }

    @Override
    public boolean expire(Duration duration, long creation) {
        if ( duration == Duration.NEVER ) {
            return false;
        }
        return System.currentTimeMillis() - creation > duration.toSeconds();
    }
}
