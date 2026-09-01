package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.service.AccountSecurityService;
import com.adaptivemfa.acs.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AccountSecurityService accountSecurityService;
    private final AuditLogService auditLogService;
    public AdminController(AccountSecurityService accountSecurityService, AuditLogService auditLogService) {
        this.accountSecurityService = accountSecurityService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/unlock/{username}")
    public ResponseEntity<String> unlockAccount(@PathVariable String username){
        accountSecurityService.unlockAccount(username);
        auditLogService.log(
                username,
                "ADMIN",
                "ADMIN",
                0,
                0,
                "LOW",
                "ACCOUNT_UNLOCKED"
        );
        return ResponseEntity.ok("Account unlocked successfully for user: " + username);
    }
}
