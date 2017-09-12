package net.zdsoft.cache.service.impl;

import net.zdsoft.cache.dao.BaseJpaDao;
import net.zdsoft.cache.entity.BaseEntity;
import net.zdsoft.cache.service.BaseService;

import java.io.Serializable;

/**
 * @author shenke
 * @since 17-9-12下午10:20
 */
public abstract class BaseServiceImpl<T extends BaseEntity<K>, K extends Serializable> implements BaseService<T, K> {

    @Override
    public T findOne(K id) {
        return getBaseJpaDao().findOne(id);
    }

    protected abstract BaseJpaDao<T,K> getBaseJpaDao();
}
