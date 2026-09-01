package com.adaptivemfa.acs.model;

import jakarta.validation.constraints.NotBlank;

public class UnlockAccountRequest {

    @NotBlank(message = "Username is required")
    private String username;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
