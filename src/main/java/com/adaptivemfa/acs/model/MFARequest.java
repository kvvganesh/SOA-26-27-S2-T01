package com.adaptivemfa.acs.model;

import jakarta.validation.constraints.NotBlank;

public class MFARequest {
    @NotBlank
    private String username;

    @NotBlank
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
