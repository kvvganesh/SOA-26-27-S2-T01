package com.adaptivemfa.acs.dto;

import jakarta.validation.constraints.NotNull;

public record RememberLocationRequest(

        @NotNull(message = "Latitude is required")
        Double latitude,

        @NotNull(message = "Longitude is required")
        Double longitude,

        @NotNull(message = "Radius is required")
        Double radiusMeters,

        String label
) {}