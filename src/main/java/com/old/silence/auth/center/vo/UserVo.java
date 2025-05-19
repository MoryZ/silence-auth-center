package com.old.silence.auth.center.vo;


import java.math.BigInteger;
import java.util.List;

/**
 * @author moryzang
 */
public class UserVo {
    /**
     * 用户ID
     */
    private BigInteger id;

    /**
     * 用户名
     */
    private String username;

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
     * 角色ID列表
     */
    private List<BigInteger> roleIds;

    private List<String> permissions;

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

    public List<BigInteger> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<BigInteger> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
