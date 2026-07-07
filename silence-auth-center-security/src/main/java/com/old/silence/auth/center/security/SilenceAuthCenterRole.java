package com.old.silence.auth.center.security;

/**
 * @author moryzang
 */
public class SilenceAuthCenterRole {

    private String roleCode;
    private String roleName;
    private String appCode;

    public SilenceAuthCenterRole() {
    }

    public SilenceAuthCenterRole(String roleCode, String roleName, String appCode) {
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
}
