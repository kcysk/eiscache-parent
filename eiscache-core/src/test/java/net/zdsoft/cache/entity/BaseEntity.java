package net.zdsoft.cache.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author shenke
 * @since 17-9-12下午10:17
 */
@MappedSuperclass
public abstract class BaseEntity<K extends Serializable> {

    @Id
    K id;

    public abstract String getCacheEntityName();

    public K getId() {
        return id;
    }

    public void setId(K id) {
        this.id = id;
    }
}
