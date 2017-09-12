package net.zdsoft.cache.dao;

import net.zdsoft.cache.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author shenke
 * @since 17-9-12下午10:22
 */
@NoRepositoryBean
public interface BaseJpaDao<T extends BaseEntity, K extends Serializable> extends JpaRepository<T, K> {

}
