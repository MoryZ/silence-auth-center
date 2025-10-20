package com.old.silence.auth.center.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.model.UserRole;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserDao;
import com.old.silence.auth.center.infrastructure.persistence.dao.UserRoleDao;
import com.old.silence.auth.center.util.PasswordUtil;
import com.old.silence.core.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final PasswordUtil passwordUtil;

    public UserService(UserDao userDao, UserRoleDao userRoleDao,
                       PasswordUtil passwordUtil) {
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
        this.passwordUtil = passwordUtil;
    }

    public Page<User> query(Page<User> page, QueryWrapper<User> queryWrapper) {
        var userPage = userDao.selectPage(page, queryWrapper);

        var userIds = userPage.getRecords()
                .stream().map(User::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return userPage;
        }
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
        return userDao.selectById(id);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public BigInteger create(User user) {
        // 密码强度验证
        validatePasswordStrength(user.getPassword());

        String encodedPassword = passwordUtil.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        userDao.insert(user);
        // 分配角色
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }

        return user.getId();
    }

    /**
     * 密码强度验证
     */
    private void validatePasswordStrength(String password) {

        // 检查是否包含数字、字母和特殊字符
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (!hasLetter || !hasDigit || !hasSpecial) {
            throw AuthCenterMessages.PASSWORD_COMPLEXITY_NOT_MATCHED.createException();
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        // 检查用户是否存在
        User existingUser = userDao.selectById(user.getId());
        // 更新用户信息
        user.setPassword(existingUser.getPassword()); // 保持原密码不变
        userDao.updateById(user);

        // 更新角色
        if (user.getRoleIds() != null) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(BigInteger id) {
        userDao.deleteById(id);

        // 删除用户角色关联
        userRoleDao.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, id));
    }

    
    public void updateUserStatus(BigInteger id, Boolean status) {
        userDao.update(new UpdateWrapper<User>().lambda().set(User::getStatus, status)
                .eq(User::getId, id));
    }

    
    public void resetPassword(BigInteger id, String password) {

        validatePasswordStrength(password);
        // 更新密码
        var newPassword = passwordUtil.encodePassword(password);
        userDao.update(new UpdateWrapper<User>().lambda().set(User::getPassword, newPassword)
                .eq(User::getId, id));
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
                    .toList();
            userRoles.forEach(userRoleDao::insert);
        }
    }

    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }
}