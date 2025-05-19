package com.old.silence.auth.center.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.repository.UserRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserDao;

import java.math.BigInteger;

/**
 * @author moryzang
 */
@Repository
public class UserMyBatisRepository implements UserRepository {
    private final UserDao userDao;

    public UserMyBatisRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findById(BigInteger id) {
        return userDao.selectById(id);
    }



    @Override
    public User findByUsernameAndStatus(String username, Boolean status) {
        return userDao.findByUsernameAndStatus(username, status);
    }

    @Override
    public int create(User user) {
        return userDao.insert(user);
    }

    @Override
    public int update(User user) {
        return userDao.updateById(user);
    }

    @Override
    public int delete(BigInteger id) {
        return userDao.deleteById(id);
    }

    @Override
    public int updatePassword(BigInteger id, String password) {
        return userDao.updatePasswordById(password, id);
    }
}
