package com.old.silence.auth.center.infrastructure.persistence;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
        return userDao.create(user);
    }

    @Override
    public int update(User user) {
        return userDao.updateById(user);
    }

    @Override
    public int update(LambdaUpdateWrapper<User> updateWrapper) {
        return userDao.update(updateWrapper);
    }

    @Override
    public int updateStatus(Boolean status, BigInteger id) {
        return userDao.update(new UpdateWrapper<User>().lambda().set(User::getStatus, status)
                .eq(User::getId, id));
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
