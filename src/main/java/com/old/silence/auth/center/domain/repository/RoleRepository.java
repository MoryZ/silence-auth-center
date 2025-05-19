package com.old.silence.auth.center.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.Role;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * @author moryzang
 */

public interface RoleRepository {

    boolean existsByCodeAndDeleted(String code, boolean deleted);

    Set<Role> findRoleByUserId(BigInteger userId);
     /**
     * 查询所有角色
     *
     * @param page 分页信息
     * @return 角色分页列表
     */
    Page<Role> query(Page<Role> page, QueryWrapper<Role> queryWrapper);

    /**
     * 根据角色ID查找角色
     *
     * @param id 角色ID
     * @return Role 对象
     */
    Role findById(BigInteger id);

    List<Role> findByStatusAndDeleted(boolean status, boolean deleted);


    /**
     * 创建新角色
     *
     * @param role 包含角色信息的Role对象
     * @return 新创建的Role对象
     */
    int create(Role role);

    /**
     * 更新角色信息
     *
     * @param role 包含更新后角色信息的Role对象
     * @return 更新后的Role对象
     */
    int update(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    int delete(BigInteger id);


}