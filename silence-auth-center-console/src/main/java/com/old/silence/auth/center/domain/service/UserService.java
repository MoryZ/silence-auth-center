package com.old.silence.auth.center.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // 预编译的正则表达式模式，避免每次调用都重新编译
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[a-zA-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

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


    @Transactional
    public BigInteger create(User user) {
        logger.info("创建新用户：username={}", user.getUsername());
        
        // 检查用户名是否已存在
        if (existsByUsername(user.getUsername())) {
            logger.warn("用户创建失败，用户名已存在：username={}", user.getUsername());
            throw AuthCenterMessages.USERNAME_ALREADY_EXIST.createException();
        }
        
        // 密码强度验证
        validatePasswordStrength(user.getPassword());

        String encodedPassword = passwordUtil.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.create(user);
        
        // 分配角色
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }
        
        logger.info("用户创建成功：id={}, username={}", user.getId(), user.getUsername());
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

        // 使用预编译的 Pattern 检查是否包含数字、字母和特殊字符，避免每次都重新编译正则
        boolean hasLetter = LETTER_PATTERN.matcher(password).matches();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).matches();
        boolean hasSpecial = SPECIAL_PATTERN.matcher(password).matches();

        if (!hasLetter || !hasDigit || !hasSpecial) {
            throw AuthCenterMessages.PASSWORD_COMPLEXITY_NOT_MATCHED.createException();
        }

    }


    @Transactional
    public void update(User user) {
        logger.info("更新用户信息：id={}, username={}", user.getId(), user.getUsername());
        
        // 检查用户是否存在
        User existingUser = userRepository.findById(user.getId());
        
        // 处理密码更新
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // 如果提供了新密码，进行验证和编码
            logger.debug("用户更新包含密码修改：id={}", user.getId());
            validatePasswordStrength(user.getPassword());
            String encodedPassword = passwordUtil.encodePassword(user.getPassword());
            user.setPassword(encodedPassword);
        } else {
            // 如果没有提供新密码，保持原密码不变
            user.setPassword(existingUser.getPassword());
        }
        
        // 更新用户信息
        userRepository.update(user);

        // 更新角色
        if (user.getRoleIds() != null) {
            assignUserRoles(user.getId(), user.getRoleIds());
        }
        
        logger.info("用户信息更新成功：id={}", user.getId());
    }


    @Transactional
    public void modifyPassword(String username, String newPassword) {
        var user = userRepository.findByUsernameAndStatus(username, Boolean.TRUE);
        if (user == null) {
            throw AuthCenterMessages.USER_NOT_EXIST.createException(username);
        }
        userRepository.updatePassword(user.getId(), newPassword);
    }


    @Transactional
    public void delete(BigInteger id) {
        userRepository.delete(id);

        // 删除用户角色关联
        userRoleRepository.deleteByUserId(id);
    }


    public void updateUserStatus(BigInteger id, Boolean status) {
        userRepository.updateStatus(status, id);
    }


    @Transactional
    public void resetPassword(BigInteger id, String password) {
        logger.info("重置用户密码：id={}", id);
        
        // 验证用户是否存在
        User user = userRepository.findById(id);
        if (user == null) {
            logger.warn("密码重置失败，用户不存在：id={}", id);
            throw AuthCenterMessages.USER_NOT_EXIST.createException();
        }
        
        // 密码强度验证
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
        
        logger.info("密码重置成功：id={}", id);
    }


    public List<BigInteger> getUserRoleIds(BigInteger userId) {
        return userRoleRepository.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }


    @Transactional
    public void assignUserRoles(BigInteger userId, Set<BigInteger> roleIds) {
        logger.info("分配用户角色：userId={}, roleCount={}", userId, roleIds != null ? roleIds.size() : 0);
        
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
            logger.info("用户角色分配成功：userId={}, roleIds={}", userId, roleIds);
        } else {
            logger.info("清除用户所有角色：userId={}", userId);
        }
    }

    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, username)
                .eq(User::getStatus, true);
        return userRepository.findByCriteria(queryWrapper) != null;
    }


}