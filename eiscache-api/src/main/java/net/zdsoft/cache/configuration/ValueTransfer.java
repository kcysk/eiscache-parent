package net.zdsoft.cache.configuration;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface ValueTransfer<S, T> {

    T parse(S object, Class<T> tClass);

    S transfer(Object object);
}
