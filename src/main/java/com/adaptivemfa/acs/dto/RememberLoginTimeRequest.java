package com.adaptivemfa.acs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RememberLoginTimeRequest(

        @NotNull(message = "Start hour is required")
        @Min(value = 0, message = "Start hour must be between 0 and 23")
        @Max(value = 23, message = "Start hour must be between 0 and 23")
        Integer startHour,

        @NotNull(message = "End hour is required")
        @Min(value = 0, message = "End hour must be between 0 and 23")
        @Max(value = 23, message = "End hour must be between 0 and 23")
        Integer endHour

) {
}