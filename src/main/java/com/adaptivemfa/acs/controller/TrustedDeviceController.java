package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.dto.RememberDeviceRequest;
import com.adaptivemfa.acs.model.TrustedDevice;
import com.adaptivemfa.acs.service.RecentAuthenticationService;
import com.adaptivemfa.acs.service.TrustedDeviceService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/security/devices")
public class TrustedDeviceController {


    private final TrustedDeviceService service;
    private final RecentAuthenticationService recentAuthenticationService;

    public TrustedDeviceController(
            TrustedDeviceService service, RecentAuthenticationService recentAuthenticationService) {

        this.service = service;
        this.recentAuthenticationService = recentAuthenticationService;
    }


    // =========================================================
    // REMEMBER CURRENT DEVICE
    // =========================================================

    @PostMapping("/remember")
    public ResponseEntity<TrustedDevice> rememberDevice(
            @Valid @RequestBody RememberDeviceRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        if (!recentAuthenticationService.isMfaVerified(username)) {
            return ResponseEntity.status(403).build();
        }

        TrustedDevice device =
                service.rememberDevice(username, request);

        return ResponseEntity.ok(device);
    }


    // =========================================================
    // GET MY TRUSTED DEVICES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<TrustedDevice>> getDevices(
            Authentication authentication) {


        String username =
                authentication.getName();


        return ResponseEntity.ok(
                service.getDevices(username)
        );
    }


    // =========================================================
    // REMOVE MY TRUSTED DEVICE
    // =========================================================

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<String> removeDevice(
            @PathVariable String deviceId,
            Authentication authentication) {


        String username =
                authentication.getName();


        service.removeDevice(
                username,
                deviceId
        );


        return ResponseEntity.ok(
                "Trusted Device has been removed"
        );
    }
}