package net.zdsoft.cache.transfer;

/**
 * @author shenke
 * @since 2017.09.20
 */
public interface ByteTransfer {

    byte[] transfer(String s);

    String transfer(byte[] bytes);
}
