package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers *when* a user last completed a successful sign-in
 * (either a low-risk password login or a password + OTP login).
 *
 * Sensitive actions, such as changing the list of trusted devices,
 * locations and login times, are only allowed shortly after a
 * successful sign-in.
 */
@Service
public class RecentAuthenticationService {

    private static final int VALID_MINUTES = 10;

    private final ConcurrentHashMap<String, AuthenticationRecord> records =
            new ConcurrentHashMap<>();


    public void recordLowRiskAuthentication(String username) {

        records.put(
                username,
                new AuthenticationRecord(
                        false,
                        LocalDateTime.now()
                )
        );
    }


    public void recordMfaAuthentication(String username) {

        records.put(
                username,
                new AuthenticationRecord(
                        true,
                        LocalDateTime.now()
                )
        );
    }


    /**
     * True if the user completed a sign-in during the last
     * {@value #VALID_MINUTES} minutes (low-risk OR MFA).
     */
    public boolean isAuthenticated(String username) {

        AuthenticationRecord record =
                records.get(username);

        if (record == null) {
            return false;
        }

        if (isExpired(record)) {
            records.remove(username);
            return false;
        }

        return true;
    }


    /**
     * True only if the most recent sign-in went through MFA.
     * Kept for callers that want a stricter "step-up" check.
     */
    public boolean isMfaVerified(String username) {

        return isAuthenticated(username)
                && records.get(username).mfaVerified();
    }


    /**
     * Guard used by the "remember device / location / time"
     * endpoints.
     *
     * A low-risk sign-in is already trusted by the risk engine, so
     * a recent successful sign-in is enough. (Previously only an
     * MFA sign-in was accepted, so every user who logged in
     * normally received an empty HTTP 403 when trying to remember
     * anything.)
     */
    public void requireRecentAuthentication(String username) {

        if (!isAuthenticated(username)) {

            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Your sign-in has expired. Please sign in again "
                            + "to change trusted security factors."
            );
        }
    }


    public void clear(String username) {

        records.remove(username);
    }


    private boolean isExpired(
            AuthenticationRecord record) {

        return record.authenticatedAt()
                .plusMinutes(VALID_MINUTES)
                .isBefore(LocalDateTime.now());
    }


    private record AuthenticationRecord(
            boolean mfaVerified,
            LocalDateTime authenticatedAt
    ) {}
}
