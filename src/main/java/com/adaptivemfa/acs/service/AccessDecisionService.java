package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class AccessDecisionService {

    public String decideAccess(String riskLevel){
        if("LOW".equalsIgnoreCase(riskLevel)){
            return "ALLOW";
        }
        if("MEDIUM".equalsIgnoreCase(riskLevel)){
            return "MFA_REQUIRED";
        }
        return "DENY";
    }

}
