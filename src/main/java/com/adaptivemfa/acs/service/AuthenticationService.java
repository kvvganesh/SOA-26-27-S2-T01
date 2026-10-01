package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.model.MfaLoginContext;
import com.adaptivemfa.acs.model.RiskAssessment;
import com.adaptivemfa.acs.model.SecuritySuggestion;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final RecentAuthenticationService
            recentAuthenticationService;

    private final RiskAssessmentService riskAssessmentService;

    private final DeviceTrustService deviceTrustService;

    private final LocationRiskService locationRiskService;

    private final AccessDecisionService accessDecisionService;

    private final MFAService mfaService;

    private final LoginBehaviorService loginBehaviorService;

    private final AuditLogService auditLogService;

    private final AccountSecurityService accountSecurityService;

    private final UserService userService;

    private final MfaLoginContextService mfaLoginContextService;

    private final OtpSender otpSender;


    public AuthenticationService(
            RiskAssessmentService riskAssessmentService,
            DeviceTrustService deviceTrustService,
            LocationRiskService locationRiskService,
            AccessDecisionService accessDecisionService,
            MFAService mfaService,
            LoginBehaviorService loginBehaviorService,
            AuditLogService auditLogService,
            AccountSecurityService accountSecurityService,
            UserService userService,
            MfaLoginContextService mfaLoginContextService,
            RecentAuthenticationService recentAuthenticationService,
            OtpSender otpSender) {

        this.otpSender = otpSender;

        this.recentAuthenticationService = recentAuthenticationService;

        this.riskAssessmentService =
                riskAssessmentService;

        this.deviceTrustService =
                deviceTrustService;

        this.locationRiskService =
                locationRiskService;

        this.accessDecisionService =
                accessDecisionService;

        this.mfaService =
                mfaService;

        this.loginBehaviorService =
                loginBehaviorService;

        this.auditLogService =
                auditLogService;

        this.accountSecurityService =
                accountSecurityService;

        this.userService =
                userService;

        this.mfaLoginContextService =
                mfaLoginContextService;
    }


    // =========================================================
    // LOGIN AUTHENTICATION
    // =========================================================

    public AuthenticationResponse authenticate(
            String username,
            String password,
            String deviceId,
            Double latitude,
            Double longitude,
            Double accuracy,
            int loginHour) {


        AuthenticationResponse response =
                new AuthenticationResponse();


        // =====================================================
        // 1. CHECK ACCOUNT LOCK
        // =====================================================

        if (accountSecurityService.isAccountLocked(username)) {

            response.setStatus(
                    "ACCESS DENIED"
            );

            response.setMessage(
                    "Account is locked"
            );

            response.setRiskLevel(
                    "HIGH"
            );

            return response;
        }


        // =====================================================
        // 2. CHECK PASSWORD FIRST
        // =====================================================

        boolean passwordCorrect =
                userService.authenticate(
                        username,
                        password
                );


        // =====================================================
        // 3. WRONG PASSWORD
        // =====================================================

        if (!passwordCorrect) {

            int failedAttempts =
                    accountSecurityService.recordFailedAttempt(
                            username
                    );


            boolean trustedDevice =
                    deviceTrustService.isTrusted(
                            username,
                            deviceId
                    );


            boolean trustedLocation =
                    locationRiskService.isTrustedLocation(
                            username,
                            latitude,
                            longitude,
                            accuracy
                    );


            boolean unusualLoginTime =
                    loginBehaviorService.isUnusualLoginTime(
                            username,
                            loginHour
                    );


            RiskAssessment assessment =
                    riskAssessmentService.assessRisk(
                            failedAttempts,
                            trustedDevice,
                            trustedLocation,
                            unusualLoginTime,
                            1
                    );


            response.setRuleScore(
                    assessment.getScore()
            );

            response.setAiRisk(
                    assessment.getAiRisk()
            );

            response.setAiProbabilities(
                    assessment.getAiProbabilities()
            );

            response.setRiskLevel(
                    assessment.getRisklevel()
            );


            response.setStatus(
                    "ACCESS DENIED"
            );

            response.setMessage(
                    "Invalid username or password"
            );


            auditLogService.log(
                    username,
                    deviceId,
                    latitude,
                    longitude,
                    accuracy,
                    failedAttempts,
                    assessment.getScore(),
                    assessment.getRisklevel(),
                    "ACCESS DENIED"
            );


            return response;
        }


        // =====================================================
        // 4. PASSWORD CORRECT
        // =====================================================

        accountSecurityService.resetFailedAttempts(
                username
        );


        int failedAttempts = 0;


        // =====================================================
        // 5. CHECK DEVICE
        // =====================================================

        boolean trustedDevice =
                deviceTrustService.isTrusted(
                        username,
                        deviceId
                );


        // =====================================================
        // 6. CHECK LOCATION
        // =====================================================

        boolean trustedLocation =
                locationRiskService.isTrustedLocation(
                        username,
                        latitude,
                        longitude,
                        accuracy
                );


        // =====================================================
        // 7. CHECK LOGIN TIME
        // =====================================================

        boolean unusualLoginTime =
                loginBehaviorService.isUnusualLoginTime(
                        username,
                        loginHour
                );


        // =====================================================
        // 8. RISK ASSESSMENT
        // =====================================================

        RiskAssessment assessment =
                riskAssessmentService.assessRisk(
                        failedAttempts,
                        trustedDevice,
                        trustedLocation,
                        unusualLoginTime,
                        0
                );


        response.setRuleScore(
                assessment.getScore()
        );

        response.setAiRisk(
                assessment.getAiRisk()
        );

        response.setAiProbabilities(
                assessment.getAiProbabilities()
        );


        String riskLevel =
                assessment.getRisklevel();


        response.setRiskLevel(
                riskLevel
        );


        // =====================================================
        // 9. ACCESS DECISION
        // =====================================================

        String accessDecision =
                accessDecisionService.decideAccess(
                        riskLevel
                );


        // =====================================================
        // 10. LOW RISK
        // =====================================================

        if (accessDecision.equals("ALLOW")) {

            response.setStatus(
                    "AUTHENTICATED"
            );

            response.setMessage(
                    "Authentication Successful"
            );


            addSecuritySuggestions(
                    response,
                    trustedDevice,
                    trustedLocation,
                    unusualLoginTime,
                    deviceId,
                    loginHour
            );


            auditLogService.log(
                    username,
                    deviceId,
                    latitude,
                    longitude,
                    accuracy,
                    failedAttempts,
                    assessment.getScore(),
                    riskLevel,
                    "AUTHENTICATED"
            );

            recentAuthenticationService
                    .recordLowRiskAuthentication(username);
            return response;
        }


        // =====================================================
        // 11. MEDIUM RISK → MFA
        // =====================================================

        if (accessDecision.equals("MFA_REQUIRED")) {


            /*
             * Save the login context BEFORE generating
             * the OTP.
             */

            MfaLoginContext context =
                    new MfaLoginContext(
                            deviceId,
                            latitude,
                            longitude,
                            accuracy,
                            loginHour,
                            trustedDevice,
                            trustedLocation,
                            unusualLoginTime
                    );


            mfaLoginContextService.store(
                    username,
                    context
            );


            mfaService.generateOtp(
                    username
            );


            response.setStatus(
                    "MFA_REQUIRED"
            );

            response.setMessage(
                    "Additional authentication required. " +
                            "Please enter the OTP sent to " +
                            otpSender.maskedDestination(username) + "."
            );


            /*
             * We don't send suggestions yet.
             *
             * The user has not completed MFA.
             */

            auditLogService.log(
                    username,
                    deviceId,
                    latitude,
                    longitude,
                    accuracy,
                    failedAttempts,
                    assessment.getScore(),
                    riskLevel,
                    "MFA_REQUIRED"
            );


            return response;
        }


        // =====================================================
        // 12. HIGH RISK
        // =====================================================

        response.setStatus(
                "ACCESS DENIED"
        );

        response.setMessage(
                "Access denied due to high risk"
        );

        response.setRiskLevel(
                riskLevel
        );


        auditLogService.log(
                username,
                deviceId,
                latitude,
                longitude,
                accuracy,
                failedAttempts,
                assessment.getScore(),
                riskLevel,
                "ACCESS DENIED"
        );


        return response;
    }


    // =========================================================
    // MFA VERIFICATION
    // =========================================================

    public AuthenticationResponse verifyMFA(
            String username,
            String otp) {


        AuthenticationResponse response =
                new AuthenticationResponse();


        // =====================================================
        // 13. CHECK MFA CHALLENGE
        // =====================================================

        if (!mfaService.isMFARequired(username)) {

            response.setStatus(
                    "ACCESS DENIED"
            );

            response.setMessage(
                    "No active MFA challenge"
            );

            response.setRiskLevel(
                    "HIGH"
            );

            return response;
        }


        // =====================================================
        // 14. VERIFY OTP
        // =====================================================

        /*
         * The context saved when MFA was requested. Used for
         * the audit trail and the security suggestions.
         */

        MfaLoginContext context =
                mfaLoginContextService.get(
                        username
                );


        boolean verified =
                mfaService.verifyOTP(
                        username,
                        otp
                );


        // =====================================================
        // 15. MFA SUCCESS
        // =====================================================

        if (verified) {

            accountSecurityService.resetFailedAttempts(
                    username
            );


            response.setStatus(
                    "AUTHENTICATED"
            );

            response.setMessage(
                    "MFA verification successful"
            );

            response.setRiskLevel(
                    "LOW"
            );


            /*
             * Use the original login context.
             */

            if (context != null) {


                addSecuritySuggestions(
                        response,
                        context.isTrustedDevice(),
                        context.isTrustedLocation(),
                        context.isUnusualLoginTime(),
                        context.getDeviceId(),
                        context.getLoginHour()
                );


                /*
                 * The context is no longer required
                 * after successful MFA.
                 */

                mfaLoginContextService.remove(
                        username
                );
            }


            auditLogService.log(
                    username,
                    context != null ? context.getDeviceId() : "MFA",
                    context != null ? context.getLatitude() : null,
                    context != null ? context.getLongitude() : null,
                    context != null ? context.getAccuracy() : null,
                    0,
                    0,
                    "LOW",
                    "MFA_AUTHENTICATED"
            );

            recentAuthenticationService
                    .recordMfaAuthentication(username);
            return response;
        }


        // =====================================================
        // 16. MFA FAILURE
        // =====================================================

        response.setStatus(
                "ACCESS DENIED"
        );

        response.setMessage(
                "Invalid or expired OTP"
        );

        response.setRiskLevel(
                "HIGH"
        );


        auditLogService.log(
                username,
                context != null ? context.getDeviceId() : "MFA",
                context != null ? context.getLatitude() : null,
                context != null ? context.getLongitude() : null,
                context != null ? context.getAccuracy() : null,
                0,
                0,
                "HIGH",
                "MFA_FAILED"
        );


        return response;
    }


    // =========================================================
    // SECURITY SUGGESTIONS
    // =========================================================

    private void addSecuritySuggestions(
            AuthenticationResponse response,
            boolean trustedDevice,
            boolean trustedLocation,
            boolean unusualLoginTime,
            String deviceId,
            int loginHour) {


        // =====================================================
        // NEW DEVICE
        // =====================================================

        if (!trustedDevice) {

            response.addSecuritySuggestion(
                    new SecuritySuggestion(
                            "DEVICE",
                            "Would you like to remember this device?"
                    )
            );
        }


        // =====================================================
        // NEW LOCATION
        // =====================================================

        if (!trustedLocation) {

            response.addSecuritySuggestion(
                    new SecuritySuggestion(
                            "LOCATION",
                            "Would you like to remember this location?"
                    )
            );
        }


        // =====================================================
        // NEW LOGIN TIME
        // =====================================================

        if (unusualLoginTime) {

            response.addSecuritySuggestion(
                    new SecuritySuggestion(
                            "LOGIN_TIME",
                            "Would you like to remember this login time?"
                    )
            );
        }
    }
}