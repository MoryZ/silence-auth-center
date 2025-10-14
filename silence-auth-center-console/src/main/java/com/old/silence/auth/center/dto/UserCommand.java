package com.old.silence.auth.center.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.old.silence.validation.group.OnlyOnCreationValidation;

import java.math.BigInteger;
import java.util.List;

public class UserCommand {
    /**
     * 用户ID
     */
    private BigInteger id;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    @NotBlank(groups = OnlyOnCreationValidation.class)
    @Size(min = 8, max = 32)
    private String password;

    /**
     * 头像URL
     */
    private String avatar;


    /**
     * 昵称
     */
    private String nickname;


    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：0-禁用，1-启用
     */
    private Boolean status;


    /**
     * 角色ID列表
     */
    private List<BigInteger> roleIds;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<BigInteger> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<BigInteger> roleIds) {
        this.roleIds = roleIds;
    }
}