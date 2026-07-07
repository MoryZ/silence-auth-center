package com.old.silence.auth.center.infrastructure.persistence;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.repository.UserRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserDao;

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
    public boolean existsById(BigInteger id) {
        return userDao.existsById(id);
    }

    @Override
    public <T> Optional<T> findById(BigInteger id, Class<T> projectionType) {
        return userDao.findById(id, projectionType);
    }


    @Override
    public <T> IPage<T> queryPage(Page<User> page, QueryWrapper<User> queryWrapper, Class<T> projectionType) {
        return userDao.findByQuery(queryWrapper, page, projectionType);
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
        return userDao.update(user);
    }

    @Override
    public int updateNonNull(User user) {
        return userDao.updateNonNull(user);
    }

    @Override
    public int updateStatus(Boolean status, BigInteger id) {
        return userDao.updateStatusById(status, id);
    }

    @Override
    public int updatePassword(BigInteger id, String password) {
        return userDao.updatePasswordById(password, id);
    }

    @Override
    public int delete(BigInteger id) {
        return userDao.deleteById(id);
    }


}
