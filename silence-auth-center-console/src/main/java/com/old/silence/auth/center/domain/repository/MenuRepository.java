package com.old.silence.auth.center.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.enums.MenuType;
import com.old.silence.auth.center.domain.model.Menu;

import java.math.BigInteger;
import java.util.List;

/**
 * @author moryzang
 */

public interface MenuRepository {

    Page<Menu> query(Page<Menu> page, QueryWrapper<Menu> queryWrapper);

    boolean existsByParentIdAndDeleted(BigInteger parentId, boolean deleted);


    /**
     * 根据菜单ID查找菜单
     *
     * @param id 菜单ID
     * @return menu 对象
     */
    Menu findById(BigInteger id);

    /**
     * 查找所有启用且未删除的菜单列表
     *
     * @param deleted 是否删除
     * @param status  是否启用
     * @param types   菜单类型集合
     * @return 菜单列表
     */
    List<Menu> findAllByDeletedAndStatusAndTypeIn(boolean deleted, boolean status, List<MenuType> types);
     /**
     * 根据查找子菜单列表
     *
     * @param deleted  是否删除
     * @param status 是否启用
     * @return 子菜单列表
     */
    List<Menu> findAllByDeletedAndStatus(boolean deleted, boolean status);

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


    List<Menu> findByIdInAndDeletedAndStatus(List<BigInteger> menuIds, boolean deleted, boolean status);
}