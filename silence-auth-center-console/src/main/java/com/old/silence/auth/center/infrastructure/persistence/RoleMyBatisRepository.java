package com.old.silence.auth.center.infrastructure.persistence;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.auth.center.domain.model.RoleMenu;
import com.old.silence.auth.center.domain.repository.RoleRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.RoleDao;
import com.old.silence.auth.center.infrastructure.persistence.dao.RoleMenuDao;
import com.old.silence.core.util.CollectionUtils;

/**
 * @author moryzang
 */
@Repository
public class RoleMyBatisRepository implements RoleRepository {

    private final RoleDao roleDao;
    private final RoleMenuDao roleMenuDao;

    public RoleMyBatisRepository(RoleDao roleDao, RoleMenuDao roleMenuDao) {
        this.roleDao = roleDao;
        this.roleMenuDao = roleMenuDao;
    }
    @Override
    public boolean existsByCode(String code) {
        return roleDao.existsByQuery(new LambdaQueryWrapper<Role>().eq(Role::getCode, code));
    }

    @Override
    public <T> Optional<T> findById(BigInteger id, Class<T> projectionType) {
        return roleDao.findById(id , projectionType);
    }

    @Override
    public <T> List<T> findByStatus(boolean status, Class<T> projectionType) {
        return roleDao.findByQuery(new LambdaQueryWrapper<Role>().eq(Role::getStatus, status), projectionType);
    }

    @Override
    public Page<Role> query(Page<Role> page, QueryWrapper<Role> queryWrapper) {
        return roleDao.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public int create(Role role) {
        var rowsAffected = roleDao.insert(role);

        // 分配菜单权限
        if (CollectionUtils.isNotEmpty(role.getRoleMenus())) {
            assignRoleMenus(role.getId(), role.getRoleMenus());
        }
        return rowsAffected;
    }

    @Override
    public int update(Role role) {
        // 更新角色信息
        var rowsAffected = roleDao.updateById(role);

        // 更新菜单权限
        if (CollectionUtils.isNotEmpty(role.getRoleMenus()))  {
            assignRoleMenus(role.getId(), role.getRoleMenus());
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

    @Override
    public int updateStatusById(Boolean status, BigInteger id) {
        UpdateWrapper<Role> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(Role::getStatus, status)
                .eq(Role::getId, id);
        return roleDao.update(updateWrapper);   // WHE
    }

    private void assignRoleMenus(BigInteger roleId, List<RoleMenu> roleMenus) {
        // 删除原有菜单权限
        roleMenuDao.delete(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));

        // 分配新菜单权限
        roleMenuDao.insertBatch(roleMenus);
    }
}
