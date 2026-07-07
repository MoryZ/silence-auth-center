package com.old.silence.auth.center.infrastructure.persistence;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.old.silence.auth.center.domain.model.UserRole;
import com.old.silence.auth.center.domain.repository.UserRoleRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserRoleDao;

/**
 * @author moryzang
 */
@Repository
public class UserRoleMyBatisRepository implements UserRoleRepository {
    private final UserRoleDao userRoleDao;

    public UserRoleMyBatisRepository(UserRoleDao userRoleDao) {
        this.userRoleDao = userRoleDao;
    }

    @Override
    public List<UserRole> findByUserId(BigInteger userId) {
        return userRoleDao.findByUserId(userId);
    }

    @Override
    public List<UserRole> selectList(LambdaQueryWrapper<UserRole> eq) {
        return userRoleDao.selectList(eq);
    }

    @Override
    public List<UserRole> findByUserIdIn(Collection<BigInteger> userIds) {
        return userRoleDao.findByUserIdIn(userIds);
    }

    @Override
    public int bulkCreate(List<UserRole> userRoles) {
        return userRoleDao.insertBatchSomeColumn(userRoles);
    }


    @Override
    public int deleteByUserId(BigInteger userId) {
        return userRoleDao.deleteByUserId(userId);
    }

}