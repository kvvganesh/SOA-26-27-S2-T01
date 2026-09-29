package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.dto.SecurityEnrollmentRequest;
import com.adaptivemfa.acs.service.SecurityEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security/enroll")
public class SecurityEnrollmentController {

    private final SecurityEnrollmentService service;

    public SecurityEnrollmentController(
            SecurityEnrollmentService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> enroll(
            @Valid @RequestBody SecurityEnrollmentRequest request) {

        service.enroll(request);

        return ResponseEntity.ok(
                "Security information enrolled successfully"
        );
    }
}