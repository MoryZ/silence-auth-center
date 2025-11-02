package com.old.silence.auth.center.domain.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.old.silence.auth.center.security.SilenceAuthCenterContextHolder;
import com.old.silence.core.util.CollectionUtils;

/**
 * @author moryzang
 */
@Service("perm")
public class PermissionService {

    @Value("${app.super-admin.usernames:admin,root,administrator}")
    private List<String> superAdminUsernames;

    /**
     * 检查用户是否有任意一个权限（超级管理员自动通过）
     */
    public boolean hasAnyAuthority(String... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 检查是否是超级管理员
        if (isSuperAdmin(authentication)) {
            return true;
        }

        // 普通用户检查权限
        return CollectionUtils.isNotEmpty(SilenceAuthCenterContextHolder.getPermissions()) && SilenceAuthCenterContextHolder.getPermissions().stream()
                .anyMatch(auth -> Arrays.asList(permissions).contains(auth));
    }

    /**
     * 检查用户是否有特定权限（超级管理员自动通过）
     */
    public boolean hasAuthority(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isSuperAdmin(authentication)) {
            return true;
        }

        return CollectionUtils.isNotEmpty(SilenceAuthCenterContextHolder.getPermissions()) && SilenceAuthCenterContextHolder.getPermissions().contains(permission);
    }

    /**
     * 判断是否是超级管理员
     */
    private boolean isSuperAdmin(Authentication authentication) {
        String username = authentication.getName();
        return superAdminUsernames.contains(username.toLowerCase());
    }
}