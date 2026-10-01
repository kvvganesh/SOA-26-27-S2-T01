package com.adaptivemfa.acs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

        // Optional: the user may have denied location access.
        Double latitude,

        Double longitude,

        Double accuracy,

        @NotNull(message = "Login hour is required")
        @Min(value = 0, message = "Login hour must be between 0 and 23")
        @Max(value = 23, message = "Login hour must be between 0 and 23")
        Integer loginHour
) {
}