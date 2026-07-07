package com.old.silence.auth.center.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.old.silence.data.commons.domain.AuditableView;

import java.math.BigInteger;

/**
 * @author moryzang
 */
public interface UserRoleView extends AuditableView {
    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    BigInteger getId();

    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    BigInteger getUserId();

    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    BigInteger getRoleId();

    RoleView getRole();

}
