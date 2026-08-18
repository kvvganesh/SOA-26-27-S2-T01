package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.AuditLog;
import com.adaptivemfa.acs.service.AuditLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
