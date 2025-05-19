package com.old.silence.auth.center.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import com.old.silence.auth.center.domain.model.RoleMenu;
import com.old.silence.auth.center.domain.repository.RoleMenuRepository;
import com.old.silence.auth.center.infrastructure.persistence.dao.RoleMenuDao;

import java.math.BigInteger;
import java.util.List;

/**
 * @author moryzang
 */
@Repository
public class RoleMenuBatisRepository implements RoleMenuRepository {

    private final RoleMenuDao roleMenuDao;

    public RoleMenuBatisRepository(RoleMenuDao roleMenuDao) {
        this.roleMenuDao = roleMenuDao;
    }

    @Override
    public List<RoleMenu> findByRoleId(BigInteger roleId) {
        return roleMenuDao.findByRoleId(roleId);
    }

    @Override
    public int bulkInsert(List<RoleMenu> roleMenus) {
        return roleMenuDao.insertBatchSomeColumn(roleMenus);
    }

    @Override
    public List<RoleMenu> findByRoleIdIn(List<BigInteger> roleIds) {
        return roleMenuDao.findByRoleIdIn(roleIds);
    }

    @Override
    public void deleteByRoleId(BigInteger roleId) {
        roleMenuDao.deleteByRoleId(roleId);
    }
}
