package com.old.silence.auth.center.vo;


import java.util.List;

import com.old.silence.auth.center.dto.MenuDto;

public class LoginVo {
    /**
     * 访问令牌
     */
    private String token;

    private UserVo userInfo;

    private List<MenuDto> menus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserVo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserVo userInfo) {
        this.userInfo = userInfo;
    }

    public List<MenuDto> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuDto> menus) {
        this.menus = menus;
    }
}