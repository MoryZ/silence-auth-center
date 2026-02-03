package com.old.silence.auth.center.security;

import java.math.BigInteger;
import java.util.Set;


public class SilencePrincipal {
    private BigInteger userId;
    private String username;
    private String cnName;
    private Set<SilenceAuthCenterRole> roles;
    private Set<String> permissions;

    public SilencePrincipal() {
    }

    public SilencePrincipal(Set<SilenceAuthCenterRole> roles, Set<String> permissions) {
        this.roles = roles;
        this.permissions = permissions;
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

    public Set<SilenceAuthCenterRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SilenceAuthCenterRole> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
