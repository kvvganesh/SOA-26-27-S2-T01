package com.adaptivemfa.acs.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FailedAttemptTracker {

    private final Map<String, Integer> failedAttempts = new HashMap<>();

    public int getAttempts(String username) {
        return failedAttempts.getOrDefault(username, 0);
    }

    public void incrementAttempts(String username) {
        int currentAttempts = getAttempts(username);
        failedAttempts.put(username, currentAttempts + 1);
    }

    public void resetAttempts(String username) {
        failedAttempts.put(username, 0);
    }
}