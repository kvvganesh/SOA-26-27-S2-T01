package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class AccessDecisionService {

    public String decideAccess(String riskLeve){
        if("LOW".equalsIgnoreCase(riskLeve)){
            return "ALLOW";
        }
        if("MEDIUM".equalsIgnoreCase(riskLeve)){
            return "REQUIRE_MFA";
        }
        return "DENY";
    }

}
