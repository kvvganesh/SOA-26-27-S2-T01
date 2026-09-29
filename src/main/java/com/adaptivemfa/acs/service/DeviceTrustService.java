package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.repository.TrustedDeviceRepository;
import org.springframework.stereotype.Service;


@Service
public class DeviceTrustService {

    private final TrustedDeviceRepository repository;

    public DeviceTrustService(TrustedDeviceRepository repository) {
        this.repository = repository;
    }

    public boolean isTrusted(String username, String deviceId) {
        return repository.findByUsernameAndDeviceIdAndActiveTrue(username, deviceId).isPresent();

    }
}
