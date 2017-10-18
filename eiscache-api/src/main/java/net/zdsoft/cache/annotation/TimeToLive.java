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
package net.zdsoft.cache.annotation;

import java.util.concurrent.TimeUnit;

/**
 * @author shenke
 * @since 17-9-4上午12:00
 */
public enum TimeToLive {

    FOREVER(),
    ONE_DAY(TimeUnit.DAYS, 1),
    ONE_HOUR(TimeUnit.HOURS, 1),
    THIRTY_MINUTES(TimeUnit.MINUTES, 30),
    TEN_MINUTES(TimeUnit.MINUTES, 10),
    THIRTY_SECONDS(TimeUnit.SECONDS, 30),
    TEN_SECONDS(TimeUnit.SECONDS, 10);


    private TimeUnit timeUnit;
    private int account;

    TimeToLive(TimeUnit timeUnit, int account) {
        this.timeUnit = timeUnit;
        this.account = account;
    }

    TimeToLive() {
    }

    public long getTime() {
        if ( this.timeUnit == null ) {
            return Long.MAX_VALUE;
        }
        return timeUnit.toMillis(account);
    }
}
