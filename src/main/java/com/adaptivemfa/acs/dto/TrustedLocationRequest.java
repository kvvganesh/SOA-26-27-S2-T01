package com.adaptivemfa.acs.dto;

import jakarta.validation.constraints.NotNull;

public record TrustedLocationRequest(

        @NotNull(message = "Username is required")
        String username,

        @NotNull(message = "Latitude is required")
        Double latitude,

        @NotNull(message = "Longitude is required")
        Double longitude,

        @NotNull(message = "Radius is required")
        Double radiusMeters,

        String label
) {
}