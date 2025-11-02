package com.old.silence.auth.center.domain.repository;


import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.old.silence.auth.center.domain.model.UserRole;

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
     *
     * @param eq 查询条件
     * @return 用户角色关系列表
     */
    List<UserRole> selectList(LambdaQueryWrapper<UserRole> eq);

    /**
     * 根据用户ID查找用户角色李彪
     *
     * @param userIds 状态
     * @return UserRole 列表
     */
    List<UserRole> findByUserIdIn(Collection<BigInteger> userIds);

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