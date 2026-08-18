package com.adaptivemfa.acs.model;

public class LoginRequest {

    private String username;
    private String password;
    private String deviceId;
    private String location;
    private int loginHour;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLoginHour() {
        return loginHour;
    }

    public void setLoginHour(int loginHour) {
        this.loginHour = loginHour;
    }
}