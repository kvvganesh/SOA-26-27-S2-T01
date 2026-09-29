package com.adaptivemfa.acs.model;

import java.util.Map;

public class RiskAssessment {
    private int score;
    private String risklevel;

    private String aiRisk;
    private Map<String, Double> aiProbabilities;



    public String getRisklevel() {
        return risklevel;
    }

    public void setRisklevel(String risklevel) {
        this.risklevel = risklevel;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, Double> getAiProbabilities() {
        return aiProbabilities;
    }
    public void setAiProbabilities(Map<String, Double> aiProbabilities) {
        this.aiProbabilities = aiProbabilities;
    }
    public String getAiRisk() {
        return aiRisk;
    }
    public void setAiRisk(String aiRisk) {
        this.aiRisk = aiRisk;
    }
}
