package net.zdsoft.cache.service;

import net.zdsoft.cache.entity.User;

/**
 * @author shenke
 * @since 17-9-10下午1:02
 */
public interface UserService extends BaseService<User, String> {

    User findById(String id);

    void save(User user);

    User findUser(String id);
}
