package com.adaptivemfa.acs.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    /*
     * Location is optional: if the user denies the browser's
     * location permission the login continues and the location is
     * simply treated as "not trusted".
     */
    private Double latitude;

    private Double longitude;

    private Double accuracy;

    @NotNull(message = "Login hour is required")
    @Min(value = 0, message = "Login hour must be between 0 and 23")
    @Max(value = 23, message = "Login hour must be between 0 and 23")
    private Integer loginHour;


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


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }


    public Integer getLoginHour() {
        return loginHour;
    }

    public void setLoginHour(Integer loginHour) {
        this.loginHour = loginHour;
    }
}

