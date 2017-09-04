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
        return null;
    }

    @Override
    public Duration getUpdateExpire() {
        return null;
    }

    @Override
    public Duration getCreateExpire() {
        return this.duration;
    }

}
