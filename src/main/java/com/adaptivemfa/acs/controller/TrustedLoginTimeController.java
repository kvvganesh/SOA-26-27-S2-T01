package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.dto.RememberLoginTimeRequest;
import com.adaptivemfa.acs.model.TrustedLoginTime;
import com.adaptivemfa.acs.service.RecentAuthenticationService;
import com.adaptivemfa.acs.service.TrustedLoginTimeService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/security/login-times")
public class TrustedLoginTimeController {


    private final TrustedLoginTimeService service;
    private final RecentAuthenticationService recentAuthenticationService;

    public TrustedLoginTimeController(
            TrustedLoginTimeService service, RecentAuthenticationService recentAuthenticationService) {

        this.recentAuthenticationService = recentAuthenticationService;
        this.service = service;
    }


    // =========================================================
    // REMEMBER CURRENT LOGIN TIME
    // =========================================================

    @PostMapping("/remember")
    public ResponseEntity<TrustedLoginTime> rememberTime(
            @Valid @RequestBody RememberLoginTimeRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        recentAuthenticationService.requireRecentAuthentication(username);

        TrustedLoginTime time =
                service.rememberTime(username, request);

        return ResponseEntity.ok(time);
    }


    // =========================================================
    // GET MY TRUSTED LOGIN TIMES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<TrustedLoginTime>> getTimes(
            Authentication authentication) {


        String username =
                authentication.getName();


        return ResponseEntity.ok(
                service.getTimes(username)
        );
    }


    // =========================================================
    // REMOVE MY TRUSTED LOGIN TIME
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeTime(
            @PathVariable Long id,
            Authentication authentication) {


        String username =
                authentication.getName();


        service.removeTime(
                username,
                id
        );


        return ResponseEntity.ok(
                "Trusted login time removed"
        );
    }
}