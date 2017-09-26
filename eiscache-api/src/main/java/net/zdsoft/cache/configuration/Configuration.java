package net.zdsoft.cache.configuration;

import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.transfer.ByteTransfer;
import net.zdsoft.cache.transfer.ValueTransfer;

/**
 * @author shenke
 * @since 17-9-3下午11:27
 */
public interface Configuration {

    /**
     * 过期时间配置
     */
    ExpiryPolicy getExpiry();

    /**
     * 数据类型转换接口
     */
    ValueTransfer getValueTransfer();

    /**
     * 字节转换接口
     */
    ByteTransfer getByteTransfer();

    Class<?> getKeyType();
}
