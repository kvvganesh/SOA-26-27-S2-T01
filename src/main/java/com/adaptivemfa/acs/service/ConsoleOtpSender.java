package com.adaptivemfa.acs.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * DEVELOPMENT ONLY - prints the OTP to the backend console.
 *
 * Active only when the "dev" profile is on:
 *   SPRING_PROFILES_ACTIVE=dev   (or  spring.profiles.active=dev)
 *
 * Never enable this in production: OTPs must not end up in log files.
 */
@Service
@Profile("dev")
public class ConsoleOtpSender implements OtpSender {

    @Override
    public void send(
            String username,
            String otp,
            OtpPurpose purpose) {

        System.out.println("=================================");
        System.out.println("   DEV OTP (" + purpose + ")");
        System.out.println("User: " + username);
        System.out.println("OTP: " + otp);
        System.out.println("Expires in: 2 minutes");
        System.out.println("=================================");
    }

    @Override
    public String maskedDestination(String username) {

        return "the server console (development mode)";
    }
}
