package com.old.silence.auth.center.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.auth.center.domain.model.RoleMenu;
import com.old.silence.auth.center.domain.repository.RoleRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.RoleDao;
import com.old.silence.auth.center.infrastructure.persistence.dao.RoleMenuDao;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author moryzang
 */
@Repository
public class RoleMyBatisRepository implements RoleRepository {

    private final RoleDao roleDao;
    private final RoleMenuDao roleMenuDao;

    @Override
    public boolean existsByCodeAndDeleted(String code, boolean deleted) {
        return roleDao.existsByCodeAndDeleted(code, deleted);
    }

    @Override
    public Set<Role> findRoleByUserId(BigInteger userId) {
        return roleDao.findRoleByUserId(userId);
    }

    @Override
    public Page<Role> query(Page<Role> page, QueryWrapper<Role> queryWrapper) {
        return roleDao.selectPage(page, queryWrapper);
    }

    public RoleMyBatisRepository(RoleDao roleDao, RoleMenuDao roleMenuDao) {
        this.roleDao = roleDao;
        this.roleMenuDao = roleMenuDao;
    }


    @Override
    public Role findById(BigInteger id) {
        return roleDao.selectById(id);
    }

    @Override
    public List<Role> findByStatusAndDeleted(boolean status, boolean deleted) {
        return roleDao.findByStatusAndDeleted(status, deleted);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(Role role) {
        var rowsAffected = roleDao.insert(role);

        // 分配菜单权限
        if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            assignRoleMenus(role.getId(), role.getMenuIds());
        }
        return rowsAffected;
    }

    @Override
    public int update(Role role) {
        // 更新角色信息
        var rowsAffected = roleDao.updateById(role);

        // 更新菜单权限
        if (role.getMenuIds() != null) {
            assignRoleMenus(role.getId(), role.getMenuIds());
        }
        return rowsAffected;

    }

    @Override
    public int delete(BigInteger id) {
        var rowsAffected = roleDao.deleteById(id);

        // 删除角色菜单关联
        roleMenuDao.delete(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, id));
        return rowsAffected;
    }


    public void assignRoleMenus(BigInteger roleId, Set<BigInteger> menuIds) {
        // 删除原有菜单权限
        roleMenuDao.delete(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));

        // 分配新菜单权限
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> {
                        RoleMenu roleMenu = new RoleMenu();
                        roleMenu.setRoleId(roleId);
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            roleMenus.forEach(roleMenuDao::insert);
        }
    }
}
