package net.zdsoft.cache.expiry;

/**
 * Expiration Policies
 * @author shenke
 * @since 17-9-3下午10:25
 */
public interface ExpiryPolicy {

    boolean expire(Duration duration, long creation);

    Duration getAccessExpire();

    Duration getUpdateExpire();

    Duration getCreateExpire();
}
