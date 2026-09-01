package com.adaptivemfa.acs.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "username must not be blank")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min=8, message="Password must contain at least 8 characters")

    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
