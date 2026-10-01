package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.dto.MyAuditLogEntry;
import com.adaptivemfa.acs.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lets every signed-in user read THEIR OWN audit trail.
 *
 * (/audit/** stays ADMIN-only and shows everybody's logs.)
 *
 * SECURITY: the username always comes from the authenticated session,
 * never from a request parameter. Otherwise any user could read another
 * user's history just by changing a value in the URL.
 */
@RestController
@RequestMapping("/security/my-audit-logs")
public class MyAuditLogController {

    private final AuditLogService auditLogService;

    public MyAuditLogController(AuditLogService auditLogService) {

        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<MyAuditLogEntry>> myLogs(
            Authentication authentication,
            @RequestParam(defaultValue = "50") int limit) {

        String username = authentication.getName();

        List<MyAuditLogEntry> logs =
                auditLogService.getRecentLogsForUser(username, limit)
                        .stream()
                        .map(MyAuditLogEntry::from)
                        .toList();

        return ResponseEntity.ok(logs);
    }
}
