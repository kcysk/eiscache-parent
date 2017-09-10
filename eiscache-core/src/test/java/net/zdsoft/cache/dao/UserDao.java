package net.zdsoft.cache.dao;

import net.zdsoft.cache.annotation.Cacheable;
import net.zdsoft.cache.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author shenke
 * @since 17-9-10下午1:00
 */
public interface UserDao extends JpaRepository<User, String> {

    @Cacheable(key = "#root.args[0]")
    User findById(String id);


    @Cacheable(key = "#root.args[0]")
    @Query(value = "select * from #{#entityName} where id = ?1", nativeQuery = true)
    User findUser(String id);

}
