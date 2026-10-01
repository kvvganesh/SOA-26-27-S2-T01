package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AuditLog;
import com.adaptivemfa.acs.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Existing method used by AuthenticationService
    public void log(
            String username,
            String deviceId,
            Double latitude,
            Double longitude,
            Double accuracy,
            int riskScore,
            String riskLevel,
            String decision
    ) {

        log(
                username,
                deviceId,
                latitude,
                longitude,
                accuracy,
                0,
                riskScore,
                riskLevel,
                decision
        );
    }


    // Method with failedAttempts
    public void log(
            String username,
            String deviceId,
            Double latitude,
            Double longitude,
            Double accuracy,
            int failedAttempts,
            int riskScore,
            String riskLevel,
            String decision
    ) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUsername(username);
        auditLog.setDeviceId(deviceId);

        auditLog.setLatitude(latitude);
        auditLog.setLongitude(longitude);
        auditLog.setAccuracy(accuracy);

        auditLog.setFailedAttempts(failedAttempts);
        auditLog.setRiskScore(riskScore);
        auditLog.setRiskLevel(riskLevel);
        auditLog.setDecision(decision);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);

        System.out.println(
                "Audit log saved to database for user: " + username
        );
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
    public List<AuditLog> getLogsByUsername(String username) {
        return auditLogRepository.findByUsername(username);
    }

    public List<AuditLog> getLogsByRiskLevel(String riskLevel) {
        return auditLogRepository.findByRiskLevel(riskLevel);
    }

    public List<AuditLog> getLogsByDecision(String decision) {
        return auditLogRepository.findByDecision(decision);
    }

    public long getTotalLogs() {
        return auditLogRepository.count();
    }

    public long getSuccessfulLogins() {
        return auditLogRepository.countByDecision("AUTHENTICATED");
    }

    public long getMfaRequired() {
        return auditLogRepository.countByDecision("MFA_REQUIRED");
    }

    public long getAccessDenied() {
        return auditLogRepository.countByDecision("ACCESS DENIED");
    }

    public long getMfaAuthenticated() {
        return auditLogRepository.countByDecision("MFA_AUTHENTICATED");
    }

    public long getMfaFailed() {
        return auditLogRepository.countByDecision("MFA_FAILED");
    }

    public long getHighRiskAttempts() {
        return auditLogRepository.countByRiskLevel("HIGH");
    }

    public long getMediumRiskAttempts() {
        return auditLogRepository.countByRiskLevel("MEDIUM");
    }
    public List<AuditLog> getSuspiciousLogs(){
        return auditLogRepository.findByRiskLevel("HIGH");
    }

    public List<AuditLog> getSuspiciousLogsByUsername(String username) {
        return auditLogRepository.findByRiskLevelAndUsername("HIGH",username);
    }

    /**
     * The signed-in user's own audit trail, newest first.
     * The limit is clamped to 1..100 so a client can't ask for everything.
     */
    public List<AuditLog> getRecentLogsForUser(String username, int limit) {

        int safeLimit = Math.min(Math.max(limit, 1), 100);

        return auditLogRepository.findByUsernameOrderByTimestampDesc(
                username,
                PageRequest.of(0, safeLimit)
        );
    }

    public List<AuditLog> getRecentLogs(int limit){
       Pageable pageable= PageRequest.of(0,limit);
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}