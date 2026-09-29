package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.exception.MFAException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MFAService {

    /*
     * Separate OTP stores for different purposes.
     *
     * MFA_LOGIN:
     * OTP generated when login requires MFA.
     *
     * PASSWORD_RESET:
     * OTP generated when the user wants to reset
     * their password.
     */
    private final Map<String, String> mfaOtpStore =
            new ConcurrentHashMap<>();

    private final Map<String, LocalDateTime> mfaExpiryStore =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> mfaAttemptStore =
            new ConcurrentHashMap<>();


    private final Map<String, String> resetOtpStore =
            new ConcurrentHashMap<>();

    private final Map<String, LocalDateTime> resetExpiryStore =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> resetAttemptStore =
            new ConcurrentHashMap<>();


    private final SecureRandom secureRandom =
            new SecureRandom();

    private final OTPDeliveryService otpDeliveryService;


    public MFAService(
            OTPDeliveryService otpDeliveryService) {

        this.otpDeliveryService =
                otpDeliveryService;
    }


    /*
     * =========================================
     * LOGIN MFA OTP
     * =========================================
     */

    public String generateOtp(String username) {

        String otp = generateRandomOtp();

        mfaOtpStore.put(username, otp);

        mfaExpiryStore.put(
                username,
                LocalDateTime.now().plusMinutes(2)
        );

        mfaAttemptStore.put(username, 0);

        otpDeliveryService.sendOtp(
                username,
                otp
        );

        return otp;
    }


    public boolean verifyOTP(
            String username,
            String otp) {

        String storedOTP =
                mfaOtpStore.get(username);

        LocalDateTime expiryTime =
                mfaExpiryStore.get(username);


        if (storedOTP == null ||
                expiryTime == null) {

            return false;
        }


        if (LocalDateTime.now()
                .isAfter(expiryTime)) {

            removeMfaOTP(username);

            return false;
        }


        int attempts =
                mfaAttemptStore.getOrDefault(
                        username,
                        0
                );


        if (attempts >= 3) {

            removeMfaOTP(username);

            throw new MFAException(
                    "Maximum MFA attempts exceeded"
            );
        }


        if (storedOTP.equals(otp)) {

            removeMfaOTP(username);

            return true;
        }


        attempts++;

        mfaAttemptStore.put(
                username,
                attempts
        );


        if (attempts >= 3) {

            removeMfaOTP(username);

            throw new MFAException(
                    "Maximum MFA attempts exceeded"
            );
        }


        return false;
    }


    public boolean isMFARequired(
            String username) {

        String storedOTP =
                mfaOtpStore.get(username);

        LocalDateTime expiryTime =
                mfaExpiryStore.get(username);


        if (storedOTP == null ||
                expiryTime == null) {

            return false;
        }


        if (LocalDateTime.now()
                .isAfter(expiryTime)) {

            removeMfaOTP(username);

            return false;
        }


        return true;
    }


    /*
     * =========================================
     * PASSWORD RESET OTP
     * =========================================
     */

    public String generatePasswordResetOtp(
            String username) {

        String otp = generateRandomOtp();

        resetOtpStore.put(
                username,
                otp
        );

        resetExpiryStore.put(
                username,
                LocalDateTime.now().plusMinutes(2)
        );

        resetAttemptStore.put(
                username,
                0
        );

        otpDeliveryService.sendOtp(
                username,
                otp
        );

        return otp;
    }


    public boolean verifyPasswordResetOtp(
            String username,
            String otp) {

        String storedOTP =
                resetOtpStore.get(username);

        LocalDateTime expiryTime =
                resetExpiryStore.get(username);


        if (storedOTP == null ||
                expiryTime == null) {

            return false;
        }


        if (LocalDateTime.now()
                .isAfter(expiryTime)) {

            removePasswordResetOTP(username);

            return false;
        }


        int attempts =
                resetAttemptStore.getOrDefault(
                        username,
                        0
                );


        if (attempts >= 3) {

            removePasswordResetOTP(username);

            throw new MFAException(
                    "Maximum password reset OTP attempts exceeded"
            );
        }


        if (storedOTP.equals(otp)) {

            removePasswordResetOTP(username);

            return true;
        }


        attempts++;

        resetAttemptStore.put(
                username,
                attempts
        );


        if (attempts >= 3) {

            removePasswordResetOTP(username);

            throw new MFAException(
                    "Maximum password reset OTP attempts exceeded"
            );
        }


        return false;
    }


    /*
     * =========================================
     * COMMON OTP GENERATOR
     * =========================================
     */

    private String generateRandomOtp() {

        return String.format(
                "%06d",
                secureRandom.nextInt(1000000)
        );
    }


    /*
     * =========================================
     * REMOVE LOGIN MFA OTP
     * =========================================
     */

    private void removeMfaOTP(
            String username) {

        mfaOtpStore.remove(username);

        mfaExpiryStore.remove(username);

        mfaAttemptStore.remove(username);
    }


    /*
     * =========================================
     * REMOVE PASSWORD RESET OTP
     * =========================================
     */

    private void removePasswordResetOTP(
            String username) {

        resetOtpStore.remove(username);

        resetExpiryStore.remove(username);

        resetAttemptStore.remove(username);
    }
}

