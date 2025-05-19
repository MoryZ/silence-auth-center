package com.old.silence.auth.center.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class SilenceHallGrantedAuthority implements GrantedAuthority {

    private String roleCode;
    private String roleName;

    public SilenceHallGrantedAuthority(String roleCode, String roleName) {
        Assert.hasText(roleCode, "A granted authority textual representation is required");
        this.roleCode = roleCode;
        this.roleName = roleName;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }else {

            return obj instanceof SilenceHallGrantedAuthority &&
                    this.roleCode.equals(((SilenceHallGrantedAuthority) obj).roleCode);
        }

    }

    @Override
    public int hashCode() {
        return roleCode.hashCode();
    }

    @Override
    public String toString() {
        return "SilenceHallGrantedAuthority{" +
                "roleCode='" + roleCode + '\'' +
                '}';
    }

    @Override
    public String getAuthority() {
        return this.roleCode;
    }
}
