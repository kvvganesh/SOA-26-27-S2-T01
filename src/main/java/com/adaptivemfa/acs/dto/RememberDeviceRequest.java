package com.adaptivemfa.acs.dto;

import jakarta.validation.constraints.NotBlank;

public record RememberDeviceRequest(

        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotBlank(message = "Device name is required")
        String deviceName

) {
}