package com.old.silence.auth.center.vo;

import com.old.silence.data.commons.domain.AuditableView;

import java.math.BigInteger;
import java.util.List;

/**
 * @author moryzang
 */
public interface RoleView extends AuditableView {

    BigInteger getId();

    String getName();

    String getCode();

    String getDescription() ;

    Boolean getStatus();

    List<RoleMenuView> getRoleMenus();
}
