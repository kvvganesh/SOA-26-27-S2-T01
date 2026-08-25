package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.model.RiskAssessment;
import com.adaptivemfa.acs.util.FailedAttemptTracker;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthenticationService {

    private final RiskAssessmentService riskAssessmentService;
    private final FailedAttemptTracker failedAttemptTracker;
    private final DeviceTrustService deviceTrustService;
    private final LocationRiskService locationRiskService;
    private final AccessDecisionService accessDecisionService;
    private final MFAService mfaService;
    private final LoginBehaviorService loginBehaviorService;
    private final AuditLogService auditLogService;

    public AuthenticationService(
            RiskAssessmentService riskAssessmentService,
            FailedAttemptTracker failedAttemptTracker,
            DeviceTrustService deviceTrustService,
            LocationRiskService locationRiskService,
            AccessDecisionService accessDecisionService,
            MFAService mfaService,
            LoginBehaviorService loginBehaviorService,
            AuditLogService auditLogService) {

        this.riskAssessmentService = riskAssessmentService;
        this.failedAttemptTracker = failedAttemptTracker;
        this.deviceTrustService = deviceTrustService;
        this.locationRiskService = locationRiskService;
        this.accessDecisionService = accessDecisionService;
        this.mfaService = mfaService;
        this.loginBehaviorService = loginBehaviorService;
        this.auditLogService = auditLogService;
    }

    public AuthenticationResponse authenticate(
            String username,
            String password,
            String deviceId,
            String location,
            int loginHour) {

        AuthenticationResponse response = new AuthenticationResponse();

        boolean trustedDevice =
                deviceTrustService.isTrusted(deviceId);

        boolean trustedLocation =
                locationRiskService.isTrustedLocation(location);

        boolean unusualLoginTime =
                loginBehaviorService.isUnusualLoginTime(loginHour);

        /*
         * Correct username and password
         */
        if (username.equals("admin") && password.equals("admin123")) {

            // Successful login → reset failed attempts
            failedAttemptTracker.resetAttempts(username);

            // After successful authentication, failed attempts = 0
            int failedAttempts = 0;

            RiskAssessment assessment =
                    riskAssessmentService.assessRisk(
                            failedAttempts,
                            trustedDevice,
                            trustedLocation,
                            unusualLoginTime
                    );

            String accessDecision =
                    accessDecisionService.decideAccess(
                            assessment.getRisklevel()
                    );

            /*
             * LOW → Authentication successful
             * MEDIUM → MFA required
             * HIGH → Access denied
             */

            if (accessDecision.equals("ALLOW")) {

                response.setStatus("AUTHENTICATED");
                response.setMessage("Authentication Successful");
                response.setRiskLevel(assessment.getRisklevel());

                auditLogService.log(
                        username,
                        deviceId,
                        location,
                        failedAttempts,
                        assessment.getScore(),
                        assessment.getRisklevel(),
                        "AUTHENTICATED"
                );

            } else if (accessDecision.equals("MFA_REQUIRED")) {

                String otp = mfaService.generateOtp(username);

                response.setStatus("MFA_REQUIRED");
                response.setMessage(
                        "Additional authentication required. OTP: " + otp
                );
                response.setRiskLevel(assessment.getRisklevel());

                auditLogService.log(
                        username,
                        deviceId,
                        location,
                        failedAttempts,
                        assessment.getScore(),
                        assessment.getRisklevel(),
                        "MFA_REQUIRED"
                );

            } else {

                response.setStatus("ACCESS DENIED");
                response.setMessage(
                        "Access denied due to high risk"
                );
                response.setRiskLevel(assessment.getRisklevel());

                auditLogService.log(
                        username,
                        deviceId,
                        location,
                        failedAttempts,
                        assessment.getScore(),
                        assessment.getRisklevel(),
                        "ACCESS DENIED"
                );
            }

        } else {

            /*
             * Wrong username/password
             */

            failedAttemptTracker.incrementAttempts(username);

            // Get NEW failed-attempt count
            int failedAttempts =
                    failedAttemptTracker.getAttempts(username);

            RiskAssessment assessment =
                    riskAssessmentService.assessRisk(
                            failedAttempts,
                            trustedDevice,
                            trustedLocation,
                            unusualLoginTime
                    );

            String accessDecision =
                    accessDecisionService.decideAccess(
                            assessment.getRisklevel()
                    );

            /*
             * Wrong password
             */

            if (accessDecision.equals("MFA_REQUIRED")) {

                String otp = mfaService.generateOtp(username);

                response.setStatus("MFA_REQUIRED");
                response.setMessage(
                        "Additional authentication required. OTP: " + otp
                );
                response.setRiskLevel(assessment.getRisklevel());

                auditLogService.log(
                        username,
                        deviceId,
                        location,
                        failedAttempts,
                        assessment.getScore(),
                        assessment.getRisklevel(),
                        "MFA_REQUIRED"
                );

            } else {

                response.setStatus("ACCESS DENIED");
                response.setMessage(
                        "Invalid username or password"
                );
                response.setRiskLevel(assessment.getRisklevel());

                auditLogService.log(
                        username,
                        deviceId,
                        location,
                        failedAttempts,
                        assessment.getScore(),
                        assessment.getRisklevel(),
                        "ACCESS DENIED"
                );
            }
        }

        return response;
    }

    public AuthenticationResponse verifyMFA(
            String username,
            String otp) {

        AuthenticationResponse response =
                new AuthenticationResponse();

        boolean verified =
                mfaService.verifyOTP(username, otp);

        if (verified) {

            response.setStatus("AUTHENTICATED");
            response.setMessage(
                    "MFA verification successful"
            );
            response.setRiskLevel("LOW");
            auditLogService.log(
                    username,
                    "MFA",
                    "MFA",
                    0,
                    0,
                    "LOW",
                    "MFA_AUTHENTICATED"
            );

        } else {

            response.setStatus("ACCESS DENIED");
            response.setMessage(
                    "Invalid or expired OTP"
            );
            response.setRiskLevel("HIGH");

            auditLogService.log(
                    username,
                    "MFA",
                    "MFA",
                    0,
                    0,
                    "HIGH",
                    "MFA_FAILED"
            );
        }

        return response;
    }
}