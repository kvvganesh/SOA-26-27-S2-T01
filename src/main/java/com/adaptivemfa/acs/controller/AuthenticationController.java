package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.model.*;
import com.adaptivemfa.acs.service.AuthenticationService;
import com.adaptivemfa.acs.service.AuthenticationSessionService;
import com.adaptivemfa.acs.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    private final AuthenticationService authenticationService;
    private final AuthenticationSessionService authenticationSessionService;
    private final UserService userService;


    public AuthenticationController(
            AuthenticationService authenticationService,
            AuthenticationSessionService authenticationSessionService,
            UserService userService) {

        this.authenticationService =
                authenticationService;

        this.authenticationSessionService =
                authenticationSessionService;

        this.userService =
                userService;
    }


// =========================================================
// LOGIN
// =========================================================

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {


        AuthenticationResponse response =
                authenticationService.authenticate(
                        request.getUsername(),
                        request.getPassword(),
                        request.getDeviceId(),
                        request.getLatitude(),
                        request.getLongitude(),
                        request.getAccuracy(),
                        request.getLoginHour()
                );


        // =================================================
        // SUCCESSFUL LOW-RISK AUTHENTICATION
        // =================================================

        if ("AUTHENTICATED".equals(
                response.getStatus())) {


            /*
             * AuthenticationService has already
             * verified the password and risk.
             *
             * Now create the actual Spring Security
             * authenticated session.
             */

            authenticationSessionService
                    .createAuthenticatedSession(
                            request.getUsername(),
                            httpRequest,
                            httpResponse
                    );


            return ResponseEntity.ok(response);
        }


        // =================================================
        // ACCESS DENIED
        // =================================================

        else if ("ACCESS DENIED".equals(
                response.getStatus())) {


            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }


        // =================================================
        // MFA REQUIRED
        // =================================================

        else if ("MFA_REQUIRED".equals(
                response.getStatus())) {


            /*
             * IMPORTANT:
             *
             * Do NOT create the authenticated
             * Spring Security session yet.
             *
             * The password is correct, but the
             * additional MFA factor is still pending.
             */

            return ResponseEntity.ok(response);
        }


        return ResponseEntity.ok(response);
    }


// =========================================================
// VERIFY MFA
// =========================================================

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthenticationResponse> verifyMFA(
            @Valid @RequestBody MFARequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {


        AuthenticationResponse response =
                authenticationService.verifyMFA(
                        request.getUsername(),
                        request.getOtp()
                );


        // =================================================
        // MFA SUCCESS
        // =================================================

        if ("AUTHENTICATED".equals(
                response.getStatus())) {


            /*
             * OTP has now been successfully verified.
             *
             * Therefore the user has completed
             * the complete authentication process.
             *
             * Create the real Spring Security session.
             */

            authenticationSessionService
                    .createAuthenticatedSession(
                            request.getUsername(),
                            httpRequest,
                            httpResponse
                    );


            return ResponseEntity.ok(response);
        }


        // =================================================
        // MFA FAILURE
        // =================================================

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


// =========================================================
// CHANGE PASSWORD
// =========================================================

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {


        userService.changePassword(
                request.getUsername(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );


        AuthenticationResponse response =
                new AuthenticationResponse();


        response.setStatus(
                "SUCCESS"
        );


        response.setMessage(
                "Password changed successfully"
        );


        response.setRiskLevel(
                "LOW"
        );


        return ResponseEntity.ok(response);
    }


// =========================================================
// FORGOT PASSWORD
// =========================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthenticationResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {


        /*
         * Generate OTP only if the account exists.
         *
         * The UserService does not reveal whether
         * the username exists.
         */

        userService.generatePasswordResetOtp(
                request.getUsername()
        );


        AuthenticationResponse response =
                new AuthenticationResponse();


        response.setStatus(
                "OTP_GENERATED"
        );


        /*
         * Do NOT return the OTP in the API response.
         *
         * This prevents username enumeration and
         * keeps the OTP outside the API response.
         */

        response.setMessage(
                "If the account exists, password reset instructions have been sent."
        );


        response.setRiskLevel(
                "LOW"
        );


        return ResponseEntity.ok(response);
    }


// =========================================================
// RESET PASSWORD
// =========================================================

    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {


        userService.resetPassword(
                request.getUsername(),
                request.getOtp(),
                request.getNewPassword()
        );


        AuthenticationResponse response =
                new AuthenticationResponse();


        response.setStatus(
                "PASSWORD_RESET"
        );


        response.setMessage(
                "Password reset successfully"
        );


        response.setRiskLevel(
                "LOW"
        );


        return ResponseEntity.ok(response);
    }
}