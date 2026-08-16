package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.RiskAssessment;
import org.springframework.stereotype.Service;


public class RiskAssesmentService {
public RiskAssessment assessRisk(int failedAttempts){
    RiskAssessment assessment = new RiskAssessment();
    int score;
    if(failedAttempts<=2){
        score=0;
    }
    else if(failedAttempts<=5){
        score=20;
    }
    else{
        score=40;
    }
    String riskLevel;
    if(score<30){
        riskLevel="LOW";
    }
    else if(score<60){
        riskLevel="MEDIUM";
    }
    else{
        riskLevel="HIGH";
    }
    assessment.setScore(score);
    assessment.setRisklevel(riskLevel);
    return assessment;
}

}
