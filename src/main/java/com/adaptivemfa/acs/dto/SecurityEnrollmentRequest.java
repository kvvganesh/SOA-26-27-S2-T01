package com.adaptivemfa.acs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SecurityEnrollmentRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Device ID is required")
        String deviceId,

        String deviceName,

        @NotNull(message = "Latitude is required")
        Double latitude,

        @NotNull(message = "Longitude is required")
        Double longitude,

        @NotNull(message = "Location accuracy is required")
        Double accuracy,

        @NotNull(message = "Login hour is required")
        Integer loginHour
) {
}