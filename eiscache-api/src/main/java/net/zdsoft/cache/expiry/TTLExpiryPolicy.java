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
