package com.old.silence.auth.center.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.model.UserRole;
import com.old.silence.auth.center.domain.repository.UserRepository;
import com.old.silence.auth.center.domain.repository.UserRoleRepository;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.util.PasswordUtil;
import com.old.silence.core.util.CollectionUtils;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordUtil passwordUtil;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository,
                       PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordUtil = passwordUtil;
    }

    public Page<User> query(Page<User> page, QueryWrapper<User> queryWrapper) {
        var userPage = userRepository.queryPage(page, queryWrapper);

        var userIds = userPage.getRecords()
                .stream().map(User::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return userPage;
        }
        var userRoles = userRoleRepository.findByUserIdIn(userIds);
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
        return userRepository.findById(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public BigInteger create(User user) {
        // 密码强度验证
        validatePasswordStrength(user.getPassword());

        String encodedPassword = passwordUtil.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.create(user);
        // 分配角色
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }

        return user.getId();
    }

    public BigInteger register(User user) {
        String encodedPassword = passwordUtil.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        user.setFirstLogin(true);
        user.setForceChangePassword(true);

        userRepository.create(user);

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
        User existingUser = userRepository.findById(user.getId());
        // 更新用户信息
        user.setPassword(existingUser.getPassword()); // 保持原密码不变
        userRepository.update(user);

        // 更新角色
        if (user.getRoleIds() != null) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }
    }


    public void modifyPassword(String username, String newPassword) {
        var user = userRepository.findByUsernameAndStatus(username, Boolean.TRUE);
        if (user == null) {
            throw AuthCenterMessages.USER_NOT_EXIST.createException(username);
        }
        userRepository.updatePassword(user.getId(), newPassword);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(BigInteger id) {
        userRepository.delete(id);

        // 删除用户角色关联
        userRoleRepository.deleteByUserId(id);
    }


    public void updateUserStatus(BigInteger id, Boolean status) {
        userRepository.updateStatus(status, id);
    }


    public void resetPassword(BigInteger id, String password) {

        validatePasswordStrength(password);
        // 更新密码
        var newPassword = passwordUtil.encodePassword(password);

        var updateWrapper = new UpdateWrapper<User>().lambda()
                .set(User::getPassword, newPassword)
                .set(User::getFirstLogin, false)
                .set(User::getForceChangePassword, false)
                .set(User::getPasswordChangedTime, Instant.now())
                .eq(User::getId, id);
        userRepository.update(updateWrapper);
    }


    public List<BigInteger> getUserRoleIds(BigInteger userId) {
        return userRoleRepository.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }


    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(BigInteger userId, Set<BigInteger> roleIds) {
        // 删除原有角色
        userRoleRepository.deleteByUserId(userId);

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
            userRoleRepository.bulkCreate(userRoles);
        }
    }

    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, username)
                .eq(User::getStatus, true);
        return userRepository.findByCriteria(queryWrapper) != null;
    }


}