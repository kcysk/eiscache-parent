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
