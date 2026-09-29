package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RecentAuthenticationService {

    private final ConcurrentHashMap<String, AuthenticationRecord> records =
            new ConcurrentHashMap<>();

    public void recordLowRiskAuthentication(String username) {

        System.out.println(
                "RECENT AUTH: Recording LOW authentication for: "
                        + username
        );

        records.put(
                username,
                new AuthenticationRecord(
                        true,
                        false,
                        LocalDateTime.now()
                )
        );

        System.out.println(
                "RECENT AUTH: Stored records = "
                        + records.keySet()
        );
    }

    public void recordMfaAuthentication(String username) {

        System.out.println(
                "RECENT AUTH: Recording MFA authentication for: "
                        + username
        );

        records.put(
                username,
                new AuthenticationRecord(
                        true,
                        true,
                        LocalDateTime.now()
                )
        );

        System.out.println(
                "RECENT AUTH: Stored records = "
                        + records.keySet()
        );
    }

    public boolean isAuthenticated(String username) {

        AuthenticationRecord record =
                records.get(username);

        System.out.println(
                "RECENT AUTH CHECK: username = "
                        + username
        );

        System.out.println(
                "RECENT AUTH CHECK: record = "
                        + record
        );

        if (record == null) {
            return false;
        }

        return !isExpired(record);
    }

    public boolean isMfaVerified(String username) {

        AuthenticationRecord record =
                records.get(username);

        System.out.println(
                "MFA AUTH CHECK: username = "
                        + username
        );

        System.out.println(
                "MFA AUTH CHECK: record = "
                        + record
        );

        if (record == null) {
            return false;
        }

        if (isExpired(record)) {

            records.remove(username);

            System.out.println(
                    "MFA AUTH CHECK: authentication expired"
            );

            return false;
        }

        return record.mfaVerified();
    }

    private boolean isExpired(
            AuthenticationRecord record) {

        return record.authenticatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());
    }

    private record AuthenticationRecord(
            boolean authenticated,
            boolean mfaVerified,
            LocalDateTime authenticatedAt
    ) {}
}