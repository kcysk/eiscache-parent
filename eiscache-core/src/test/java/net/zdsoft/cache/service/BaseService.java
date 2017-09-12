package net.zdsoft.cache.service;

import net.zdsoft.cache.annotation.CacheDefault;
import net.zdsoft.cache.annotation.Cacheable;
import net.zdsoft.cache.entity.BaseEntity;

import java.io.Serializable;

/**
 * @author shenke
 * @since 17-9-12下午10:17
 */
@CacheDefault(cacheName = "#getFirstGenericType(#root.targetClass).newInstance().getCacheEntityName()")
public interface BaseService<T extends BaseEntity<K>, K extends Serializable> {

    @Cacheable(key = "#root.args[0]", entityId = "#root.args[0]")
    T findOne(K id);
}
