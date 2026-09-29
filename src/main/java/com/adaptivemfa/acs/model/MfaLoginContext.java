package com.adaptivemfa.acs.model;

public class MfaLoginContext {

    private final String deviceId;

    private final Double latitude;

    private final Double longitude;

    private final Double accuracy;

    private final int loginHour;

    private final boolean trustedDevice;

    private final boolean trustedLocation;

    private final boolean unusualLoginTime;


    public MfaLoginContext(
            String deviceId,
            Double latitude,
            Double longitude,
            Double accuracy,
            int loginHour,
            boolean trustedDevice,
            boolean trustedLocation,
            boolean unusualLoginTime) {

        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.loginHour = loginHour;
        this.trustedDevice = trustedDevice;
        this.trustedLocation = trustedLocation;
        this.unusualLoginTime = unusualLoginTime;
    }


    public String getDeviceId() {
        return deviceId;
    }


    public Double getLatitude() {
        return latitude;
    }


    public Double getLongitude() {
        return longitude;
    }


    public Double getAccuracy() {
        return accuracy;
    }


    public int getLoginHour() {
        return loginHour;
    }


    public boolean isTrustedDevice() {
        return trustedDevice;
    }


    public boolean isTrustedLocation() {
        return trustedLocation;
    }


    public boolean isUnusualLoginTime() {
        return unusualLoginTime;
    }
}