package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.model.LoginRequest;
import com.adaptivemfa.acs.model.MFARequest;
import com.adaptivemfa.acs.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request){
        AuthenticationResponse response=authenticationService.authenticate(
                request.getUsername(),
                request.getPassword(),
                request.getDeviceId(),
                request.getLocation(),
                request.getLoginHour()

        );
       if(response.getStatus().equals("AUTHENTICATED")){
           return ResponseEntity.ok(response);
       }
       else if(response.getStatus().equals("ACCESS DENIED")){
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
       }
       else if(response.getStatus().equals("MFA_REQUIRED")){
           return ResponseEntity.ok(response);
       }
       return ResponseEntity.ok(response);

    }

    @PostMapping("/auth/verify-mfa")
    public ResponseEntity<AuthenticationResponse> verifyMFA(@Valid @RequestBody MFARequest request){
        AuthenticationResponse response=authenticationService.verifyMFA(
                request.getUsername(),
                request.getOtp()
        );

        if("AUTHENTICATED".equals(response.getStatus())){
            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

    }

}
