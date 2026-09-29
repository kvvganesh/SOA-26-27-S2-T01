package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class OTPDeliveryService {

    /*
     * Development OTP delivery service.
     *
     * In production, this service should be connected
     * to an email provider, SMS provider, or
     * authenticator application.
     *
     * IMPORTANT:
     * OTP values should never be logged in production.
     */

    public void sendOtp(
            String username,
            String otp) {

        System.out.println(
                "================================="
        );

        System.out.println(
                "          OTP DELIVERY"
        );

        System.out.println(
                "User: " + username
        );

        System.out.println(
                "OTP: " + otp
        );

        System.out.println(
                "Expires in: 2 minutes"
        );

        System.out.println(
                "================================="
        );
    }
}