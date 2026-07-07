package com.old.silence.auth.center.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.old.silence.data.commons.domain.AuditableView;

import java.math.BigInteger;

/**
 * @author moryzang
 */
public interface UserView extends AuditableView {

    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    BigInteger getId();

    String getUsername();

    Boolean getStatus();

    String getAvatar();

    String getNickname();

    String getEmail();

    String getPhone();

    Boolean getFirstLogin();

    Boolean getForceChangePassword();

}
