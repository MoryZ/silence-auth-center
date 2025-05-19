package com.old.silence.auth.center.security;


import com.old.silence.auth.center.domain.model.Role;

import java.math.BigInteger;
import java.util.Set;


public class SilencePrincipal {
    private BigInteger userId;
    private String username;
    private String cnName;
    private Set<Role> roles;


    public SilencePrincipal(Set<Role> roles) {
        this.roles = roles;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
