package com.old.silence.auth.center.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class ResetUserPasswordCommand {


    @NotBlank
    @Size(min = 8, max = 32)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}