package com.old.silence.auth.center.domain.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.model.UserRole;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserDao;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserRoleDao;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;

    public UserService(UserDao userDao, UserRoleDao userRoleDao) {
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
    }

    public Page<User> query(Page<User> page, QueryWrapper<User> queryWrapper) {
        var userPage = userDao.selectPage(page, queryWrapper);

        var userIds = userPage.getRecords()
                .stream().map(User::getId).collect(Collectors.toList());
        var userRoles = userRoleDao.findByUserIdIn(userIds);
        var groupingByUserIdRoleIdsMap = userRoles.stream()
                .collect(Collectors.groupingBy(UserRole::getUserId, Collectors.mapping(
                        UserRole::getRoleId,
                        Collectors.toSet()  // 去重
                )));
        for (User record : userPage.getRecords()) {
            record.setRoleIds(groupingByUserIdRoleIdsMap.get(record.getId()));
        }
        return userPage;
    }

    
    public User findById(BigInteger id) {
        User user = userDao.selectById(id);
        return user;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public BigInteger create(User user) {
        // 创建用户
        user.setPassword("123456"); // 默认密码
        user.setDeleted(false);
        userDao.insert(user);

        // 分配角色
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }

        return user.getId();
    }




    @Transactional(rollbackFor = Exception.class)
    public void update(User sysUser) {
        // 检查用户是否存在
        User existingUser = userDao.selectById(sysUser.getId());
        // 更新用户信息
        User user = new User();
        BeanUtils.copyProperties(sysUser, user);
        user.setPassword(existingUser.getPassword()); // 保持原密码不变
        userDao.updateById(user);

        // 更新角色
        if (sysUser.getRoleIds() != null) {
            assignUserRoles(user.getId(), sysUser.getRoleIds());
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(BigInteger id) {
        // 检查用户是否存在
        User user = userDao.selectById(id);

        // 逻辑删除用户
        user.setDeleted(true);
        userDao.updateById(user);

        // 删除用户角色关联
        userRoleDao.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, id));
    }

    
    public void updateUserStatus(BigInteger id, Boolean status) {
        // 检查用户是否存在
        User user = userDao.selectById(id);

        // 更新状态
        user.setStatus(status);
        userDao.updateById(user);
    }

    
    public void resetPassword(BigInteger id, String password) {
        // 检查用户是否存在
        User user = userDao.selectById(id);

        // 更新密码
        user.setPassword(password);
        userDao.updateById(user);
    }

    
    public List<BigInteger> getUserRoleIds(BigInteger userId) {
        return userRoleDao.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(BigInteger userId, Set<BigInteger> roleIds) {
        // 删除原有角色
        userRoleDao.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));

        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            userRoles.forEach(userRoleDao::insert);
        }
    }

    private boolean isUsernameExists(String username) {
        return userDao.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)) > 0;
    }
} 