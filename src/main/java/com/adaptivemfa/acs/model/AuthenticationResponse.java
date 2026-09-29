package com.adaptivemfa.acs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthenticationResponse {

    private String status;

    private String message;

    private String riskLevel;

    private int ruleScore;

    private String aiRisk;

    private Map<String, Double> aiProbabilities;

    private List<SecuritySuggestion> securitySuggestions =
            new ArrayList<>();


    // ==========================================
    // STATUS
    // ==========================================

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    // ==========================================
    // MESSAGE
    // ==========================================

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    // ==========================================
    // RISK LEVEL
    // ==========================================

    public String getRiskLevel() {
        return riskLevel;
    }


    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }


    // ==========================================
    // RULE SCORE
    // ==========================================

    public int getRuleScore() {
        return ruleScore;
    }


    public void setRuleScore(int ruleScore) {
        this.ruleScore = ruleScore;
    }


    // ==========================================
    // AI RISK
    // ==========================================

    public String getAiRisk() {
        return aiRisk;
    }


    public void setAiRisk(String aiRisk) {
        this.aiRisk = aiRisk;
    }


    // ==========================================
    // AI PROBABILITIES
    // ==========================================

    public Map<String, Double> getAiProbabilities() {
        return aiProbabilities;
    }


    public void setAiProbabilities(
            Map<String, Double> aiProbabilities) {

        this.aiProbabilities =
                aiProbabilities;
    }


    // ==========================================
    // SECURITY SUGGESTIONS
    // ==========================================

    public List<SecuritySuggestion>
    getSecuritySuggestions() {

        return securitySuggestions;
    }


    public void setSecuritySuggestions(
            List<SecuritySuggestion> securitySuggestions) {

        this.securitySuggestions =
                securitySuggestions;
    }


    public void addSecuritySuggestion(
            SecuritySuggestion suggestion) {

        this.securitySuggestions.add(
                suggestion
        );
    }
}