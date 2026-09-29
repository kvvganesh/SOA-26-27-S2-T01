package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.RememberDeviceRequest;
import com.adaptivemfa.acs.dto.TrustedDeviceRequest;
import com.adaptivemfa.acs.model.TrustedDevice;
import com.adaptivemfa.acs.repository.TrustedDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustedDeviceService {

    private final TrustedDeviceRepository repository;


    public TrustedDeviceService(
            TrustedDeviceRepository repository) {

        this.repository = repository;
    }


    // =========================================================
    // REGISTRATION / SECURITY ENROLLMENT
    // =========================================================

    public TrustedDevice addDevice(
            TrustedDeviceRequest request) {

        TrustedDevice device =
                new TrustedDevice(
                        request.username(),
                        request.deviceId(),
                        request.deviceName()
                );

        return repository.save(device);
    }


    // =========================================================
    // REMEMBER DEVICE AFTER AUTHENTICATION
    // =========================================================

    public TrustedDevice rememberDevice(
            String username,
            RememberDeviceRequest request) {

        /*
         * Prevent duplicate active trusted devices.
         */

        boolean alreadyTrusted =
                isTrusted(
                        username,
                        request.deviceId()
                );

        if (alreadyTrusted) {

            return repository
                    .findByUsernameAndDeviceIdAndActiveTrue(
                            username,
                            request.deviceId()
                    )
                    .orElseThrow();
        }


        TrustedDevice device =
                new TrustedDevice(
                        username,
                        request.deviceId(),
                        request.deviceName()
                );

        return repository.save(device);
    }


    // =========================================================
    // GET USER'S TRUSTED DEVICES
    // =========================================================

    public List<TrustedDevice> getDevices(
            String username) {

        return repository
                .findByUsernameAndActiveTrue(username);
    }


    // =========================================================
    // CHECK TRUSTED DEVICE
    // =========================================================

    public boolean isTrusted(
            String username,
            String deviceId) {

        return repository
                .findByUsernameAndDeviceIdAndActiveTrue(
                        username,
                        deviceId
                )
                .isPresent();
    }


    // =========================================================
    // REMOVE TRUSTED DEVICE
    // =========================================================

    public void removeDevice(
            String username,
            String deviceId) {

        repository
                .findByUsernameAndDeviceIdAndActiveTrue(
                        username,
                        deviceId
                )
                .ifPresent(device -> {

                    device.setActive(false);

                    repository.save(device);
                });
    }
}