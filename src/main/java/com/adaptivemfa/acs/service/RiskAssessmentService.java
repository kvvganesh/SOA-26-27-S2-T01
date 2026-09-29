package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.RiskAssessment;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RiskAssessmentService {

    private final AIService aiService;

    public RiskAssessmentService(AIService aiService) {
        this.aiService = aiService;
    }

    public RiskAssessment assessRisk(
            int failedAttempts,
            boolean trustedDevice,
            boolean trustedLocation,
            boolean unusualLoginTime,
            int passwordFailed) {

        RiskAssessment assessment =
                new RiskAssessment();


        // =========================================
        // 1. RULE-BASED RISK SCORE
        // =========================================

        int score = 0;


        // Failed attempts
        if (failedAttempts <= 2) {

            score += 0;

        } else if (failedAttempts <= 5) {

            score += 40;

        } else {

            score += 80;
        }


        // Untrusted device
        if (!trustedDevice) {

            score += 30;
        }


        // Untrusted location
        if (!trustedLocation) {

            score += 20;
        }


        // Unusual login time
        if (unusualLoginTime) {

            score += 20;
        }


        // =========================================
        // 2. CONVERT VALUES FOR AI MODEL
        // =========================================

        int trustedDeviceValue =
                trustedDevice ? 1 : 0;

        int trustedLocationValue =
                trustedLocation ? 1 : 0;

        int unusualTimeValue =
                unusualLoginTime ? 1 : 0;


        // =========================================
        // 3. CALL RANDOM FOREST AI
        // =========================================

        Map<String, Object> aiResponse =
                aiService.predictRisk(
                        failedAttempts,
                        trustedDeviceValue,
                        trustedLocationValue,
                        unusualTimeValue,
                        passwordFailed
                );


        // =========================================
        // 4. GET AI PREDICTION
        // =========================================

        String aiRisk =
                (String) aiResponse.get(
                        "predicted_risk"
                );


        // =========================================
        // 5. GET AI PROBABILITIES
        // =========================================

        Map<String, Double> probabilities =
                (Map<String, Double>)
                        aiResponse.get(
                                "probabilities"
                        );


        // =========================================
        // 6. CALCULATE RULE-BASED RISK LEVEL
        // =========================================

        String ruleRisk;

        if (score < 30) {

            ruleRisk = "LOW";

        } else if (score < 60) {

            ruleRisk = "MEDIUM";

        } else {

            ruleRisk = "HIGH";
        }


        // =========================================
        // 7. HYBRID RISK DECISION
        // =========================================

        String finalRisk;


        /*
         * If either the rule engine OR AI
         * identifies HIGH risk,
         * final risk becomes HIGH.
         */

        if (
                "HIGH".equalsIgnoreCase(ruleRisk)
                        ||
                        "HIGH".equalsIgnoreCase(aiRisk)
        ) {

            finalRisk = "HIGH";
        }


        /*
         * If neither is HIGH but either system
         * identifies MEDIUM risk,
         * final risk becomes MEDIUM.
         */

        else if (
                "MEDIUM".equalsIgnoreCase(ruleRisk)
                        ||
                        "MEDIUM".equalsIgnoreCase(aiRisk)
        ) {

            finalRisk = "MEDIUM";
        }


        /*
         * Both systems consider the login LOW.
         */

        else {

            finalRisk = "LOW";
        }


        // =========================================
        // 8. STORE RESULTS
        // =========================================

        assessment.setScore(score);

        // Final HYBRID risk
        assessment.setRisklevel(finalRisk);

        // AI prediction
        assessment.setAiRisk(aiRisk);

        // AI probabilities
        assessment.setAiProbabilities(
                probabilities
        );


        // =========================================
        // 9. DISPLAY RESULTS IN CONSOLE
        // =========================================

        System.out.println(
                "=============================="
        );

        System.out.println(
                "RULE SCORE: " + score
        );

        System.out.println(
                "RULE RISK: " + ruleRisk
        );

        System.out.println(
                "AI RISK: " + aiRisk
        );

        System.out.println(
                "AI PROBABILITIES: " + probabilities
        );

        System.out.println(
                "FINAL HYBRID RISK: " + finalRisk
        );

        System.out.println(
                "=============================="
        );


        return assessment;
    }
}

