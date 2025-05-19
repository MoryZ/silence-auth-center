package com.old.silence.auth.center.domain.repository;


import com.old.silence.auth.center.domain.model.UserRole;

import java.math.BigInteger;
import java.util.List;

/**
 * @author moryzang
 */

public interface UserRoleRepository {



    /**
     * 根据用户ID查找用户
     *
     * @param userId 状态
     * @return UserRole 对象
     */
    List<UserRole> findByUserId(BigInteger userId);
    /**
     * 创建新用户
     *
     * @param userRoles 包含用户信息的User对象
     * @return 新创建的User对象
     */
    int bulkCreate(List<UserRole> userRoles);


    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    int deleteByUserId(BigInteger userId);




}