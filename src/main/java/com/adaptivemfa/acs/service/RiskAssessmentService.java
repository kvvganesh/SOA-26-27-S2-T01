package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.RiskAssessment;
import org.springframework.stereotype.Service;

@Service
public class RiskAssessmentService {

    public RiskAssessment assessRisk(int failedAttempts, boolean trustedDevice, boolean trustedLocation) {

        RiskAssessment assessment = new RiskAssessment();

        int score = 0;

        if (failedAttempts <= 2) {
            score += 0;
        }
        else if (failedAttempts <= 5) {
            score += 40;
        }
        else {
            score += 80;
        }

        if(!trustedDevice) {
            score+=30;
        }
        if(!trustedLocation) {
            score+=20;
        }

        String riskLevel;

        if (score < 30) {
            riskLevel = "LOW";
        }
        else if (score < 60) {
            riskLevel = "MEDIUM";
        }
        else {
            riskLevel = "HIGH";
        }

        assessment.setScore(score);
        assessment.setRisklevel(riskLevel);

        return assessment;
    }
}