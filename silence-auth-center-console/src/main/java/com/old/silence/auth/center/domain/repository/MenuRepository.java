package com.old.silence.auth.center.domain.repository;

import java.math.BigInteger;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.enums.MenuType;

/**
 * @author moryzang
 */

public interface MenuRepository {

    Page<Menu> query(Page<Menu> page, QueryWrapper<Menu> queryWrapper);

    boolean existsByParentId(BigInteger parentId);


    /**
     * 根据菜单ID查找菜单
     *
     * @param id 菜单ID
     * @return menu 对象
     */
    Menu findById(BigInteger id);

    /**
     * 查找所有启用的菜单列表（自动过滤已删除）
     *
     * @param status  是否启用
     * @param types   菜单类型集合
     * @return 菜单列表
     */
    List<Menu> findAllByStatusAndTypeIn(boolean status, List<MenuType> types);

    /**
     * 查找所有启用的菜单（自动过滤已删除）
     *
     * @param status  是否启用
     * @return 子菜单列表
     */
    List<Menu> findAllByStatus(boolean status);

    /**
     * 创建新菜单
     *
     * @param menu 包含菜单信息的menu对象
     * @return 新创建的menu对象
     */
    int create(Menu menu);

    /**
     * 更新菜单信息
     *
     * @param menu 包含更新后菜单信息的menu对象
     * @return 更新后的menu对象
     */
    int update(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void delete(BigInteger id);


    List<Menu> findByIdInAndStatus(List<BigInteger> menuIds, boolean status);
}