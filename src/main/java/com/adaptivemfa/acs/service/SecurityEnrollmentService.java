package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.SecurityEnrollmentRequest;
import com.adaptivemfa.acs.model.TrustedDevice;
import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.model.TrustedLoginTime;
import com.adaptivemfa.acs.repository.TrustedLocationRepository;
import com.adaptivemfa.acs.repository.TrustedLoginTimeRepository;
import org.springframework.stereotype.Service;

@Service
public class SecurityEnrollmentService {

    private final UserService userService;
    private final TrustedDeviceService trustedDeviceService;
    private final TrustedLocationRepository trustedLocationRepository;
    private final TrustedLoginTimeRepository trustedLoginTimeRepository;

    public SecurityEnrollmentService(
            UserService userService,
            TrustedDeviceService trustedDeviceService,
            TrustedLocationRepository trustedLocationRepository,
            TrustedLoginTimeRepository trustedLoginTimeRepository) {

        this.userService = userService;
        this.trustedDeviceService = trustedDeviceService;
        this.trustedLocationRepository = trustedLocationRepository;
        this.trustedLoginTimeRepository = trustedLoginTimeRepository;
    }

    public void enroll(
            SecurityEnrollmentRequest request) {

        /*
         * Verify that the username/password
         * combination is valid.
         */
        if (!userService.authenticate(
                request.username(),
                request.password())) {

            throw new RuntimeException(
                    "Invalid username or password"
            );
        }

        /*
         * -----------------------------------------
         * 1. TRUSTED DEVICE
         * -----------------------------------------
         */

        String deviceName =
                request.deviceName();

        if (deviceName == null ||
                deviceName.isBlank()) {

            deviceName = "Registered Device";
        }

        trustedDeviceService.addDevice(
                new com.adaptivemfa.acs.dto.TrustedDeviceRequest(
                        request.username(),
                        request.deviceId(),
                        deviceName
                )
        );


        /*
         * -----------------------------------------
         * 2. TRUSTED LOCATION
         * -----------------------------------------
         */

        TrustedLocation location =
                new TrustedLocation(
                        request.username(),
                        request.latitude(),
                        request.longitude(),
                        100.0,
                        "Registered Location"
                );

        trustedLocationRepository.save(location);


        /*
         * -----------------------------------------
         * 3. TRUSTED LOGIN TIME
         * -----------------------------------------
         */

        TrustedLoginTime loginTime =
                new TrustedLoginTime(
                        request.username(),
                        request.loginHour(),
                        request.loginHour()
                );

        trustedLoginTimeRepository.save(loginTime);
    }
}