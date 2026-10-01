package com.adaptivemfa.acs.dto;

import com.adaptivemfa.acs.model.AuditLog;

import java.time.LocalDateTime;

/**
 * What a normal user is allowed to see about their own sign-in history.
 *
 * We return this small DTO instead of the AuditLog entity so that
 * database details (and any field added to the entity later) don't leak
 * to the browser by accident.
 */
public record MyAuditLogEntry(
        Long id,
        LocalDateTime timestamp,
        String decision,
        String riskLevel,
        int riskScore,
        int failedAttempts,
        String deviceId,
        Double latitude,
        Double longitude
) {

    public static MyAuditLogEntry from(AuditLog log) {

        return new MyAuditLogEntry(
                log.getId(),
                log.getTimestamp(),
                log.getDecision(),
                log.getRiskLevel(),
                log.getRiskScore(),
                log.getFailedAttempts(),
                log.getDeviceId(),
                log.getLatitude(),
                log.getLongitude()
        );
    }
}
