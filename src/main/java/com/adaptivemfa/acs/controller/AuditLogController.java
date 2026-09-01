package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.AuditLog;
import com.adaptivemfa.acs.model.AuditStatistics;
import com.adaptivemfa.acs.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/audit")

public class AuditLogController {
    private final AuditLogService auditLogService;
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/logs")
    public List<AuditLog> getAuditLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping("/user/{username}")
    public List<AuditLog> getLogsByUsername(
            @PathVariable String username) {

        return auditLogService.getLogsByUsername(username);
    }

    @GetMapping("/risk/{riskLevel}")
    public List<AuditLog> getLogsByRiskLevel(
            @PathVariable String riskLevel) {

        return auditLogService.getLogsByRiskLevel(riskLevel);
    }

    @GetMapping("/decision/{decision}")
    public List<AuditLog> getLogsByDecision(
            @PathVariable String decision) {

        return auditLogService.getLogsByDecision(decision);
    }

    @GetMapping("/statistics")
    public AuditStatistics getStatistics() {

        AuditStatistics statistics = new AuditStatistics();

        statistics.setTotalLogs(
                auditLogService.getTotalLogs()
        );

        statistics.setSuccessfulLogins(
                auditLogService.getSuccessfulLogins()
        );

        statistics.setMfaRequired(
                auditLogService.getMfaRequired()
        );

        statistics.setAccessDenied(
                auditLogService.getAccessDenied()
        );

        statistics.setMfaAuthenticated(
                auditLogService.getMfaAuthenticated()
        );

        statistics.setMfaFailed(
                auditLogService.getMfaFailed()
        );

        statistics.setHighRiskAttempts(
                auditLogService.getHighRiskAttempts()
        );

        statistics.setMediumRiskAttempts(
                auditLogService.getMediumRiskAttempts()
        );

        return statistics;
    }
    @GetMapping("/suspicious")
        public List<AuditLog> getSuspiciousLogs() {
            return auditLogService.getSuspiciousLogs();
    }
    @GetMapping("/suspicious/{username}")
        public List<AuditLog> getSuspiciousLogsByUsername(
                @PathVariable String username){
        return auditLogService.getSuspiciousLogsByUsername(username);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogs(@RequestParam int limit)
    {
        if(limit<1 || limit>100){
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(auditLogService.getRecentLogs(limit));

    }

}
