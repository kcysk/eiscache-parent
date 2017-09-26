package net.zdsoft.cache.support;

import net.zdsoft.cache.configuration.Configuration;
import net.zdsoft.cache.expiry.Duration;
import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.expiry.TTLExpiryPolicy;
import net.zdsoft.cache.transfer.ByteTransfer;
import net.zdsoft.cache.transfer.ValueTransfer;

/**
 * @author shenke
 * @since 2017.09.26
 */
public class GlobalConfiguration implements Configuration {

    private ExpiryPolicy    defaultExpiry = new TTLExpiryPolicy(Duration.NEVER);
    private ValueTransfer   valueTransfer = new JSONValueTransfer();
    private ByteTransfer    byteTransfer  = new DefaultByteTransfer();

    @Override
    public ExpiryPolicy getExpiry() {
        return defaultExpiry;
    }

    @Override
    public ValueTransfer getValueTransfer() {
        return valueTransfer;
    }

    @Override
    public ByteTransfer getByteTransfer() {
        return byteTransfer;
    }

    @Override
    public Class<?> getKeyType() {
        return String.class;
    }
}
