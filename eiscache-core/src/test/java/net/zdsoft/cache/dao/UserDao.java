package net.zdsoft.cache.dao;

import net.zdsoft.cache.entity.User;
import org.springframework.data.jpa.repository.Query;

/**
 * @author shenke
 * @since 17-9-10下午1:00
 */
public interface UserDao extends BaseJpaDao<User, String> {

    User findById(String id);


    @Query(value = "select * from #{#entityName} where id = ?1", nativeQuery = true)
    User findUser(String id);

}
