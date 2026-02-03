package com.old.silence.auth.center.domain.repository;


import java.math.BigInteger;
import java.util.List;

import com.old.silence.auth.center.domain.model.RoleMenu;

/**
 * @author moryzang
 */

public interface RoleMenuRepository {

    List<RoleMenu> findByRoleId(BigInteger roleId);

    int bulkInsert(List<RoleMenu> roleMenus);

    List<RoleMenu> findByRoleIdIn(List<BigInteger> roleIds);


    void deleteByRoleId(BigInteger roleId);

}