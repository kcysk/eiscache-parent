package net.zdsoft.cache.service.impl;

import net.zdsoft.cache.dao.UserDao;
import net.zdsoft.cache.entity.User;
import net.zdsoft.cache.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author shenke
 * @since 17-9-10下午1:03
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public User findById(String id) {
        return userDao.findById(id);
    }

    @Override
    public void save(User user) {
        userDao.save(user);
    }

    @Override
    public User findUser(String id) {
        return userDao.findUser(id);
    }
}
