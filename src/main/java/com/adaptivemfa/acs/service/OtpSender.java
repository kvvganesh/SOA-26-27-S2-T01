package com.adaptivemfa.acs.service;

/**
 * How a one-time password reaches the user.
 *
 * The rest of the application only knows this interface, so the delivery
 * channel (console, e-mail, SMS ...) can be swapped without touching
 * MFAService or AuthenticationService.
 */
public interface OtpSender {

    /**
     * Delivers the OTP.
     *
     * @throws com.adaptivemfa.acs.exception.ApiException when it can't be delivered
     */
    void send(String username, String otp, OtpPurpose purpose);

    /**
     * A safe-to-display description of where the OTP goes,
     * e.g. "g***@gmail.com". Never contains the OTP itself.
     */
    String maskedDestination(String username);
}
