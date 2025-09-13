package com.old.silence.auth.center.security;

import java.math.BigInteger;

public class SilenceAuthCenterUser {

    private BigInteger userId;
    private String username;
    private String cnName;

    public SilenceAuthCenterUser() {
    }

    public SilenceAuthCenterUser(BigInteger userId, String username, String cnName) {
        this.userId = userId;
        this.username = username;
        this.cnName = cnName;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public boolean isAdmin(String username){
        return "admin".equals(username);
    }
}
