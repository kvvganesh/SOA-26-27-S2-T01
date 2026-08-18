package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MFAService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, LocalDateTime> expiryStore = new HashMap<>();
    private final Map<String, Integer> attemptStore = new HashMap<>();

    public String generateOtp(String username) {

        String otp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        otpStore.put(username, otp);

        expiryStore.put(
                username,
                LocalDateTime.now().plusMinutes(2)
        );

        // Reset attempts whenever a new OTP is generated
        attemptStore.put(username, 0);

        return otp;
    }

    public boolean verifyOTP(String username, String otp) {

        String storedOTP = otpStore.get(username);
        LocalDateTime expiryTime = expiryStore.get(username);

        // OTP doesn't exist
        if (storedOTP == null || expiryTime == null) {
            return false;
        }

        // OTP expired
        if (LocalDateTime.now().isAfter(expiryTime)) {
            removeOTP(username);
            return false;
        }

        // Check maximum attempts
        int attempts = attemptStore.getOrDefault(username, 0);

        if (attempts >= 3) {
            removeOTP(username);
            return false;
        }

        // Correct OTP
        if (storedOTP.equals(otp)) {
            removeOTP(username);
            return true;
        }

        // Wrong OTP
        attempts++;
        attemptStore.put(username, attempts);

        // Three wrong attempts → invalidate OTP
        if (attempts >= 3) {
            removeOTP(username);
        }

        return false;
    }

    private void removeOTP(String username) {

        otpStore.remove(username);
        expiryStore.remove(username);
        attemptStore.remove(username);
    }
}