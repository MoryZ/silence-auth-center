package com.old.silence.auth.center.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.enums.MenuType;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.domain.repository.MenuRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.MenuDao;
import com.old.silence.auth.center.infrastructure.persistence.dao.RoleMenuDao;

import java.math.BigInteger;
import java.util.List;

/**
 * @author moryzang
 */
@Repository
public class MenuMyBatisRepository implements MenuRepository {

    private final MenuDao menuDao;
    private final RoleMenuDao roleMenuDao;

    public MenuMyBatisRepository(MenuDao menuDao, RoleMenuDao roleMenuDao) {
        this.menuDao = menuDao;
        this.roleMenuDao = roleMenuDao;
    }

    @Override
    public Page<Menu> query(Page<Menu> page, QueryWrapper<Menu> queryWrapper) {
        return menuDao.selectPage(page, queryWrapper);
    }

    @Override
    public boolean existsByParentIdAndDeleted(BigInteger parentId, boolean deleted) {
        return menuDao.existsByParentIdAndDeleted(parentId, deleted);
    }

    @Override
    public Menu findById(BigInteger id) {
        return menuDao.selectById(id);
    }

    @Override
    public List<Menu> findAllByDeleted(boolean deleted) {
        var queryWrapper = new LambdaQueryWrapper<Menu>()
                .eq(Menu::getDeleted, 0)
                .orderByAsc(Menu::getSort);

        return menuDao.selectList(queryWrapper);
    }

    @Override
    public List<Menu> findByIdInAndDeletedAndTypeInAndStatus(List<BigInteger> menuIds, boolean deleted, List<MenuType> types, boolean status) {
        var queryWrapper = new LambdaQueryWrapper<Menu>()
                .in(Menu::getId, menuIds)
                .eq(Menu::getDeleted, deleted)
                .in(Menu::getType, types)
                .eq(Menu::getStatus, status)
                .orderByAsc(Menu::getSort);
        return menuDao.selectList(queryWrapper);
    }

    @Override
    public int create(Menu menu) {
        return menuDao.insert(menu);
    }

    @Override
    public int update(Menu menu) {
        return menuDao.updateById(menu);
    }

    @Override
    public void delete(BigInteger id) {
        menuDao.deleteById(id);
        // 删除角色菜单关联
        roleMenuDao.deleteByMenuId(id);
    }
}
