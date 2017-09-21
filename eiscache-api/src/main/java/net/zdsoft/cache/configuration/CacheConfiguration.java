package net.zdsoft.cache.configuration;

import net.zdsoft.cache.listener.CacheEventListener;
import net.zdsoft.cache.expiry.ExpiryPolicy;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface CacheConfiguration<K> extends Configuration<K> {

    <L extends CacheEventListener> L getListener(Class<L> listenerClass);

    <E extends ExpiryPolicy> E getExpiry();

    ValueTransfer getValueTransfer();
}
