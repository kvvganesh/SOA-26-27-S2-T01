package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class LocationRiskService {

    public boolean isTrustedLocation(String location){
        System.out.println("location received: " + location);
        if(location==null){
            return false;
        }

        return "Chennai".equalsIgnoreCase(location.trim());
    }
}
