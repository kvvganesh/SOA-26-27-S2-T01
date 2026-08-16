package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class LocationRiskService {

    public boolean isTrustedLocation(String location){
        return "Chennai".equalsIgnoreCase(location);
    }
}
