package com.old.silence.auth.center.dto;


import com.old.silence.data.commons.annotation.RelationalQueryProperty;
import com.old.silence.data.commons.converter.Part;

public class UserQuery {
    /**
     * 用户名
     */
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String username;

    /**
     * 昵称
     */
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String nickname;

    /**
     * 手机号
     */
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String phone;

    /**
     * 状态：0-禁用，1-启用
     */
    @RelationalQueryProperty(type = Part.Type.SIMPLE_PROPERTY)
    private Boolean status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}