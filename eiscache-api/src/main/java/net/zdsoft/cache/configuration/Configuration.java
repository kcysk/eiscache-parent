package net.zdsoft.cache.configuration;

/**
 * @author shenke
 * @since 17-9-3下午11:27
 */
public interface Configuration<K> {

    Class<K> getKeyType();

}
