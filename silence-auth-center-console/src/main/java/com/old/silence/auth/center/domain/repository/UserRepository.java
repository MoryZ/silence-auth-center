package com.old.silence.auth.center.domain.repository;


import java.math.BigInteger;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.User;

/**
 * @author moryzang
 */

public interface UserRepository {


    /**
     *
     * @param queryWrapper 查询条件
     * @return 用户
     */
    User findByCriteria(QueryWrapper<User> queryWrapper);

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return User 对象
     */
    User findById(BigInteger id);

    /**
     * 分页查询
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return Page<User> 分页结果
     */
    Page<User> queryPage(Page<User> page, QueryWrapper<User> queryWrapper);


    /**
     * 根据用户ID查找用户
     *
     * @param username 用户名
     * @param status   状态
     * @return User 对象
     */
    User findByUsernameAndStatus(String username, Boolean status);

    /**
     * 创建新用户
     *
     * @param user 包含用户信息的User对象
     * @return 新创建的User对象
     */
    int create(User user);

    /**
     * 更新用户信息
     *
     * @param user 包含更新后用户信息的User对象
     * @return 更新后的User对象
     */
    int update(User user);

    int update(LambdaUpdateWrapper<User> updateWrapper);

    /**
     * 更新用户状态
     *
     * @param status 用户状态
     * @return 影响条数
     */
    int updateStatus(Boolean status, BigInteger id);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    int delete(BigInteger id);

    /**
     * 更新用户的密码
     *
     * @param id       用户ID
     * @param password 新密码
     * @return 更新后的User对象
     */
    int updatePassword(BigInteger id, String password);



}