package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.AuditLog;
import com.adaptivemfa.acs.model.AuditStatistics;
import com.adaptivemfa.acs.service.AccountSecurityService;
import com.adaptivemfa.acs.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AccountSecurityService accountSecurityService;
    private final AuditLogService auditLogService;

    public AdminController(
            AccountSecurityService accountSecurityService,
            AuditLogService auditLogService) {

        this.accountSecurityService = accountSecurityService;
        this.auditLogService = auditLogService;
    }


    /*
     * =========================================
     * 1. UNLOCK ACCOUNT
     * =========================================
     */

    @PostMapping("/unlock/{username}")
    public ResponseEntity<String> unlockAccount(
            @PathVariable String username) {

        accountSecurityService.unlockAccount(username);

        auditLogService.log(
                username,
                "ADMIN",
                null,
                null,
                null,
                0,
                0,
                "LOW",
                "ACCOUNT_UNLOCKED"
        );

        return ResponseEntity.ok(
                "Account unlocked successfully for user: "
                        + username
        );
    }


    /*
     * =========================================
     * 2. GET ALL AUDIT LOGS
     * =========================================
     */

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAllLogs() {

        return ResponseEntity.ok(
                auditLogService.getAllLogs()
        );
    }


    /*
     * =========================================
     * 3. GET USER'S AUDIT LOGS
     * =========================================
     */

    @GetMapping("/audit-logs/user/{username}")
    public ResponseEntity<List<AuditLog>> getLogsByUsername(
            @PathVariable String username) {

        return ResponseEntity.ok(
                auditLogService.getLogsByUsername(username)
        );
    }


    /*
     * =========================================
     * 4. GET HIGH-RISK LOGS
     * =========================================
     */

    @GetMapping("/audit-logs/high-risk")
    public ResponseEntity<List<AuditLog>> getHighRiskLogs() {

        return ResponseEntity.ok(
                auditLogService.getSuspiciousLogs()
        );
    }


    /*
     * =========================================
     * 5. GET HIGH-RISK LOGS FOR USER
     * =========================================
     */

    @GetMapping("/audit-logs/high-risk/{username}")
    public ResponseEntity<List<AuditLog>> getHighRiskLogsByUsername(
            @PathVariable String username) {

        return ResponseEntity.ok(
                auditLogService.getSuspiciousLogsByUsername(username)
        );
    }


    /*
     * =========================================
     * 6. GET RECENT LOGS
     * =========================================
     */

    @GetMapping("/audit-logs/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogs(
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(
                auditLogService.getRecentLogs(limit)
        );
    }


    /*
     * =========================================
     * 7. GET LOGS BY RISK LEVEL
     * =========================================
     */

    @GetMapping("/audit-logs/risk/{riskLevel}")
    public ResponseEntity<List<AuditLog>> getLogsByRiskLevel(
            @PathVariable String riskLevel) {

        return ResponseEntity.ok(
                auditLogService.getLogsByRiskLevel(riskLevel)
        );
    }


    /*
     * =========================================
     * 8. GET LOGS BY DECISION
     * =========================================
     */

    @GetMapping("/audit-logs/decision/{decision}")
    public ResponseEntity<List<AuditLog>> getLogsByDecision(
            @PathVariable String decision) {

        return ResponseEntity.ok(
                auditLogService.getLogsByDecision(decision)
        );
    }


    /*
     * =========================================
     * 9. SECURITY STATISTICS
     * =========================================
     */

    @GetMapping("/statistics")
    public ResponseEntity<AuditStatistics> getStatistics() {

        AuditStatistics statistics =
                new AuditStatistics();

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

        return ResponseEntity.ok(statistics);

    }

}

