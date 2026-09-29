package com.adaptivemfa.acs.dto;

public record AIPredictionRequest(
        int failed_attempts,
        int trusted_device,
        int trusted_location,
        int unusual_time,
        int password_failed
) {
}