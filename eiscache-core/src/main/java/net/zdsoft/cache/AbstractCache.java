package net.zdsoft.cache;

import net.zdsoft.cache.configuration.CacheConfiguration;
import net.zdsoft.cache.configuration.ValueTransfer;
import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.listener.CacheEventListener;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class AbstractCache implements Cache {

    private CacheConfiguration cacheConfiguration;

    public AbstractCache(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    public AbstractCache() {
        this.cacheConfiguration = new CacheConfiguration() {
            @Override
            public CacheEventListener getListener(Class listenerClass) {
                return null;
            }

            @Override
            public ExpiryPolicy getExpiry() {
                return null;
            }

            @Override
            public ValueTransfer getValueTransfer() {
                return null;
            }

            @Override
            public Class getKeyType() {
                return String.class;
            }
        };
    }
}
