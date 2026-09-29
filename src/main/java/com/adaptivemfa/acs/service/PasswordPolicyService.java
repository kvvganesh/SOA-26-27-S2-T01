package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {

    public void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new RuntimeException(
                    "Password cannot be empty"
            );
        }

        if (password.length() < 8) {
            throw new RuntimeException(
                    "Password must contain at least 8 characters"
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException(
                    "Password must contain at least one uppercase letter"
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException(
                    "Password must contain at least one lowercase letter"
            );
        }

        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException(
                    "Password must contain at least one number"
            );
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new RuntimeException(
                    "Password must contain at least one special character"
            );
        }
    }
}

