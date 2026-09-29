package com.adaptivemfa.acs.dto;

public record TrustedDeviceRequest (
    String username,
    String deviceId,
    String deviceName)
{}
