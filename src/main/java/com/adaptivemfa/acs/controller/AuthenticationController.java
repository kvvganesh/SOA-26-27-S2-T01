package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.model.LoginRequest;
import com.adaptivemfa.acs.model.MFARequest;
import com.adaptivemfa.acs.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService=authenticationService;
    }

    @PostMapping("/auth/login")
    public AuthenticationResponse login(@RequestBody LoginRequest request){
        return authenticationService.authenticate(
                request.getUsername(),
                request.getPassword(),
                request.getDeviceId(),
                request.getLocation(),
                request.getLoginHour()

        );
    }

    @PostMapping("/auth/verify-mfa")
    public AuthenticationResponse verifyMFA(@RequestBody MFARequest request){
        return authenticationService.verifyMFA(
                request.getUsername(),
                request.getOtp()
        );

    }

}
