package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.model.ChangePasswordRequest;
import com.adaptivemfa.acs.model.LoginRequest;
import com.adaptivemfa.acs.model.MFARequest;
import com.adaptivemfa.acs.service.AuthenticationService;
import com.adaptivemfa.acs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService=authenticationService;
        this.userService=userService;
    }

    @PostMapping("/login")
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

    @PostMapping("/verify-mfa")
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

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request){
        userService.changePassword(
                request.getUsername(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        AuthenticationResponse response=new AuthenticationResponse();

        response.setStatus("SUCCESS");
        response.setMessage("Password changed successfully");
        response.setRiskLevel("LOW");

        return ResponseEntity.ok(response);
    }




}
