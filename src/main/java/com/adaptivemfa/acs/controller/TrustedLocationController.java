package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.dto.RememberLocationRequest;
import com.adaptivemfa.acs.dto.TrustedLocationRequest;
import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.service.RecentAuthenticationService;
import com.adaptivemfa.acs.service.TrustedLocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/security/locations")
public class TrustedLocationController {

    private final TrustedLocationService service;
    private final RecentAuthenticationService recentAuthenticationService;

    public TrustedLocationController(
            TrustedLocationService service,
            RecentAuthenticationService recentAuthenticationService) {

        this.service = service;
        this.recentAuthenticationService =
                recentAuthenticationService;
    }

    @PostMapping
    public ResponseEntity<TrustedLocation> addLocation(
            @Valid @RequestBody RememberLocationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        if (!recentAuthenticationService.isMfaVerified(username)) {
            return ResponseEntity.status(403).build();
        }

        TrustedLocationRequest secureRequest =
                new TrustedLocationRequest(
                        username,
                        request.latitude(),
                        request.longitude(),
                        request.radiusMeters(),
                        request.label()
                );

        TrustedLocation location =
                service.addLocation(secureRequest);

        return ResponseEntity.ok(location);
    }

    @GetMapping
    public ResponseEntity<List<TrustedLocation>> getLocations(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                service.getLocations(username)
        );
    }

    @DeleteMapping("/{latitude}/{longitude}")
    public ResponseEntity<String> removeLocation(
            @PathVariable Double latitude,
            @PathVariable Double longitude,
            Authentication authentication) {

        String username = authentication.getName();

        service.removeLocation(
                username,
                latitude,
                longitude
        );

        return ResponseEntity.ok(
                "Trusted Location has been removed"
        );
    }
}