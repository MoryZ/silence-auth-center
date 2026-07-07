package com.old.silence.auth.center.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class ModifyUserPasswordCommand {

    private String username;

    @NotBlank
    @Size(min = 8, max = 32)
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}