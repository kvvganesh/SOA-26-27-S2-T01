package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.dto.SecurityEnrollmentRequest;
import com.adaptivemfa.acs.service.RateLimitService;
import com.adaptivemfa.acs.service.SecurityEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security/enroll")
public class SecurityEnrollmentController {

    private final SecurityEnrollmentService service;
    private final RateLimitService rateLimitService;

    public SecurityEnrollmentController(
            SecurityEnrollmentService service,
            RateLimitService rateLimitService) {

        this.service = service;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ResponseEntity<String> enroll(
            @Valid @RequestBody SecurityEnrollmentRequest request) {

        rateLimitService.isAllowed("ENROLL:" + request.username());

        String message = service.enroll(request);

        return ResponseEntity.ok(message);
    }
}
