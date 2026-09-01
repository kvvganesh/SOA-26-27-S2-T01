package com.adaptivemfa.acs.model;

import java.time.LocalDateTime;

public class RegistrationResponse {
    private String username;
    private String status;
    private String message;
    private LocalDateTime timestamp;

    public RegistrationResponse(String username, String status, String message, LocalDateTime timestamp) {
        this.username = username;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }
    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
