package com.old.silence.auth.center.security;

/**
 * @author moryzang
 */
public class SilenceAuthCenterBranch {

    private String branchCode;
    private String branchName;

    public SilenceAuthCenterBranch() {
    }

    public SilenceAuthCenterBranch(String branchCode, String branchName) {
        this.branchCode = branchCode;
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
