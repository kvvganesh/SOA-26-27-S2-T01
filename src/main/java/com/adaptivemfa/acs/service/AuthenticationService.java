package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.model.RiskAssessment;
import com.adaptivemfa.acs.util.FailedAttemptTracker;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final RiskAssessmentService riskAssessmentService;
    private final FailedAttemptTracker failedAttemptTracker;
    private final DeviceTrustService deviceTrustService;
    private final LocationRiskService locationRiskService;
    private final AccessDecisionService accessDecisionService;
    public AuthenticationService(
            RiskAssessmentService riskAssessmentService,
            FailedAttemptTracker failedAttemptTracker,
            DeviceTrustService deviceTrustService,
            LocationRiskService locationRiskService,
            AccessDecisionService accessDecisionService) {

        this.riskAssessmentService = riskAssessmentService;
        this.failedAttemptTracker = failedAttemptTracker;
        this.deviceTrustService = deviceTrustService;
        this.locationRiskService = locationRiskService;
        this.accessDecisionService = accessDecisionService;
    }

    public AuthenticationResponse authenticate(
            String username,
            String password,
            String deviceId,
            String location) {

        AuthenticationResponse response = new AuthenticationResponse();

        boolean trustedDevice=deviceTrustService.isTrusted(deviceId);

        boolean trustedLocation=locationRiskService.isTrustedLocation(location);

        if (username.equals("admin") && password.equals("admin123")) {

            // Successful login → reset failed attempts
            failedAttemptTracker.resetAttempts(username);

            RiskAssessment assessment =
                    riskAssessmentService.assessRisk(0,trustedDevice,trustedLocation);
            String accessDecision= accessDecisionService.decideAccess(assessment.getRisklevel());

            if(accessDecision.equals("ALLOW")) {
                response.setStatus("AUTHENTICATED");
                response.setMessage("Authentication Successful");
            }

            else if(accessDecision.equals("REQUIRE_MFA")){
                response.setStatus("MFA_REQUIRED");
                response.setMessage("Additional authentication required");
            }
            else{
                response.setStatus("ACCESS_DENIED");
                response.setMessage("High risk login blocked");
            }
            response.setRiskLevel(assessment.getRisklevel());


        } else {

            // Wrong password → increase failed attempts
            failedAttemptTracker.incrementAttempts(username);

            // Get the NEW number of failed attempts
            int failedAttempts =
                    failedAttemptTracker.getAttempts(username);

            // Calculate risk using the new count
            RiskAssessment assessment =
                    riskAssessmentService.assessRisk(failedAttempts,trustedDevice,trustedLocation);

            String accessDecision =
                    accessDecisionService.decideAccess(
                            assessment.getRisklevel());

            if (accessDecision.equals("REQUIRE_MFA")) {

                response.setStatus("MFA_REQUIRED");
                response.setMessage("Additional authentication required");

            } else {

                response.setStatus("ACCESS DENIED");
                response.setMessage("Invalid username or password");
            }

            response.setRiskLevel(assessment.getRisklevel());
        }

        return response;
    }

    }
