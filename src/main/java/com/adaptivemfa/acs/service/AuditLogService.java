package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AuditLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogService {
    private final List<AuditLog> auditlogs = new ArrayList<>();

    public void log(
            String username,
            String deviceId,
            String location,
            int riskScore,
            String riskLevel,
            String decision
            ){
        AuditLog auditLog = new AuditLog(
                LocalDateTime.now(),
                username,
                deviceId,
                location,
                riskScore,
                riskLevel,
                decision
        );
        auditlogs.add(auditLog);
        System.out.println("Audit log created for user: "+username);

    }
    public List<AuditLog> getAllLogs() {
        return auditlogs;
    }
}
