package com.old.silence.auth.center.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class SilenceAuthCenterGrantedAuthority implements GrantedAuthority {

    private String roleCode;
    private String roleName;
    private String appCode;

    public SilenceAuthCenterGrantedAuthority(String roleCode, String roleName, String appCode) {
        Assert.hasText(roleCode, "A granted authority textual representation is required");
        Assert.hasText(appCode, "appCode is required");
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.appCode = appCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }else {

            return obj instanceof SilenceAuthCenterGrantedAuthority &&
                    this.roleCode.equals(((SilenceAuthCenterGrantedAuthority) obj).roleCode);
        }

    }

    @Override
    public int hashCode() {
        return roleCode.hashCode();
    }

    @Override
    public String toString() {
        return "SilenceAuthCenterGrantedAuthority{" +
                "roleCode='" + roleCode + '\'' +
                '}';
    }

    @Override
    public String getAuthority() {
        return this.roleCode;
    }
}
