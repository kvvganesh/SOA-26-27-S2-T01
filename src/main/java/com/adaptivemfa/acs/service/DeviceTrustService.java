package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;


@Service
public class DeviceTrustService {

    public boolean isTrusted(String deviceId){
        return "device-001".equals(deviceId);
    }
}
