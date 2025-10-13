package com.old.silence.auth.center.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserPasswordCommand {


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