package net.zdsoft.cache.support;

import net.zdsoft.cache.transfer.ByteTransfer;

import java.nio.charset.Charset;

/**
 * @author shenke
 * @since 2017.09.20
 */
public class DefaultByteTransfer implements ByteTransfer {

    private Charset charset = Charset.forName("UTF8");

    @Override
    public byte[] transfer(String s) {
        return s == null ? null : s.getBytes(charset);
    }

    @Override
    public String transfer(byte[] bytes) {
        return bytes == null ? null : new String(bytes, charset);
    }
}
