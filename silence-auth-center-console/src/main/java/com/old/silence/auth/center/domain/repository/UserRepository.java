package com.old.silence.auth.center.domain.repository;


import java.math.BigInteger;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.User;

/**
 * @author moryzang
 */

public interface UserRepository {

    /**
     * 判断id 是否存在
     *
     * @param id 主键id
     * @return 是否存在
     */
    boolean existsById(BigInteger id);

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @param projectionType 投影类型
     * @return User 对象
     */
    <T> Optional<T> findById(BigInteger id, Class<T> projectionType);

    /**
     * 分页查询
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @param projectionType 投影类型
     * @return Page<User> 分页结果
     */
    <T> IPage<T> queryPage(Page<User> page, QueryWrapper<User> queryWrapper, Class<T> projectionType);


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

    int updateNonNull(User user);

    /**
     * 更新用户状态
     *
     * @param status 用户状态
     * @return 影响条数
     */
    int updateStatus(Boolean status, BigInteger id);

    /**
     * 更新用户的密码
     *
     * @param id       用户ID
     * @param password 新密码
     * @return 更新后的User对象
     */
    int updatePassword(BigInteger id, String password);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    int delete(BigInteger id);

}