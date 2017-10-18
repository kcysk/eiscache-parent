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

import java.util.concurrent.TimeUnit;

/**
 * 持续时间
 * @author shenke
 * @since 17-9-3下午10:28
 */
public final class Duration {

    private TimeUnit timeUnit;
    private int durationAccount;

    public static final Duration NEVER = new Duration();

    public static final Duration ONE_HOUR = new Duration(TimeUnit.HOURS, 1);

    public Duration() {

    }

    public Duration(TimeUnit timeUnit, int durationAccount) {
        this.timeUnit = timeUnit;
        this.durationAccount = durationAccount;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getDurationAccount() {
        return durationAccount;
    }

    public long toSeconds() {
        return this.timeUnit.toSeconds(durationAccount);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;

        } else if (other == null || getClass() != other.getClass()) {
            return false;

        } else {
            Duration duration = (Duration) other;

            if (this.timeUnit == null && duration.timeUnit == null &&
                    this.durationAccount == duration.durationAccount) {
                return true;
            } else if (this.timeUnit != null && duration.timeUnit != null) {
                long time1 = timeUnit.toMillis(durationAccount);
                long time2 = duration.timeUnit.toMillis(duration.durationAccount);
                return time1 == time2;
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return timeUnit == null ? -1 : (int)timeUnit.toMillis(durationAccount);
    }
}
