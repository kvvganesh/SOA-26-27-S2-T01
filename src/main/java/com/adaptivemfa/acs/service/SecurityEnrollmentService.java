package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.SecurityEnrollmentRequest;
import com.adaptivemfa.acs.dto.TrustedDeviceRequest;
import com.adaptivemfa.acs.exception.ApiException;
import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.model.TrustedLoginTime;
import com.adaptivemfa.acs.repository.TrustedLocationRepository;
import com.adaptivemfa.acs.repository.TrustedLoginTimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SecurityEnrollmentService {

    private final UserService userService;
    private final AccountSecurityService accountSecurityService;
    private final TrustedDeviceService trustedDeviceService;
    private final LocationRiskService locationRiskService;
    private final TrustedLocationRepository trustedLocationRepository;
    private final TrustedLoginTimeRepository trustedLoginTimeRepository;

    public SecurityEnrollmentService(
            UserService userService,
            AccountSecurityService accountSecurityService,
            TrustedDeviceService trustedDeviceService,
            LocationRiskService locationRiskService,
            TrustedLocationRepository trustedLocationRepository,
            TrustedLoginTimeRepository trustedLoginTimeRepository) {

        this.userService = userService;
        this.accountSecurityService = accountSecurityService;
        this.trustedDeviceService = trustedDeviceService;
        this.locationRiskService = locationRiskService;
        this.trustedLocationRepository = trustedLocationRepository;
        this.trustedLoginTimeRepository = trustedLoginTimeRepository;
    }

    /**
     * First-time enrollment, called right after registration.
     *
     * @return a short human readable summary of what was enrolled
     */
    public String enroll(
            SecurityEnrollmentRequest request) {

        String username = request.username();

        /*
         * This endpoint is public, so it must not become a way to
         * brute-force passwords without the normal lock-out.
         */
        if (accountSecurityService.isAccountLocked(username)) {

            throw new ApiException(
                    HttpStatus.LOCKED,
                    "Account is temporarily locked. Please try again later."
            );
        }

        if (!userService.authenticate(
                username,
                request.password())) {

            accountSecurityService.recordFailedAttempt(username);

            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        /*
         * SECURITY: enrollment may only happen once.
         *
         * Otherwise anyone who knows (or has stolen) a password
         * could call this public endpoint, register their own
         * device / location / time as "trusted" and then log in
         * with LOW risk, bypassing MFA completely. Additional
         * factors are added later through the authenticated
         * "remember" endpoints.
         */
        boolean alreadyEnrolled =
                !trustedDeviceService.getDevices(username).isEmpty()
                        || !trustedLocationRepository
                        .findByUsernameAndActiveTrue(username).isEmpty()
                        || !trustedLoginTimeRepository
                        .findByUsernameAndActiveTrue(username).isEmpty();

        if (alreadyEnrolled) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Security enrollment has already been completed. "
                            + "Sign in to manage trusted devices, "
                            + "locations and login times."
            );
        }

        accountSecurityService.resetFailedAttempts(username);


        /*
         * 1. TRUSTED DEVICE
         */

        String deviceName = request.deviceName();

        if (deviceName == null || deviceName.isBlank()) {

            deviceName = "Registered Device";
        }

        trustedDeviceService.addDevice(
                new TrustedDeviceRequest(
                        username,
                        request.deviceId(),
                        deviceName
                )
        );


        /*
         * 2. TRUSTED LOCATION (only if a usable fix was supplied)
         */

        boolean locationSaved = false;

        Double latitude = request.latitude();
        Double longitude = request.longitude();
        Double accuracy = request.accuracy();

        if (latitude != null
                && longitude != null
                && (accuracy == null
                || accuracy <= locationRiskService
                .getMaxAccuracyMeters())) {

            TrustedLocation location =
                    new TrustedLocation(
                            username,
                            latitude,
                            longitude,
                            locationRiskService.effectiveRadius(
                                    100.0,
                                    accuracy
                            ),
                            "Registered Location"
                    );

            trustedLocationRepository.save(location);

            locationSaved = true;
        }


        /*
         * 3. TRUSTED LOGIN TIME
         */

        TrustedLoginTime loginTime =
                new TrustedLoginTime(
                        username,
                        request.loginHour(),
                        request.loginHour()
                );

        trustedLoginTimeRepository.save(loginTime);


        return locationSaved
                ? "Security information enrolled successfully"
                : "Device and login time enrolled. Location was not "
                + "saved (permission denied or too imprecise); you can "
                + "remember it after signing in.";
    }
}
