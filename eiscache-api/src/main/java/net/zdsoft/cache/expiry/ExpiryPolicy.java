package net.zdsoft.cache.expiry;

/**
 * Expiration Policies
 * @author shenke
 * @since 17-9-3下午10:25
 */
public interface ExpiryPolicy {

    default boolean expire(Duration duration, long creation) {
        if ( duration == Duration.NEVER ) {
            return false;
        }
        return System.currentTimeMillis() - creation > duration.toSeconds();
    }

    Duration getAccessExpire();

    Duration getUpdateExpire();

    Duration getCreateExpire();
}
