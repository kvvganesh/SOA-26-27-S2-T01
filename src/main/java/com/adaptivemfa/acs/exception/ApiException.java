package com.adaptivemfa.acs.exception;

import org.springframework.http.HttpStatus;

/**
 * A business-rule failure that should reach the client as a proper
 * HTTP status + JSON body (instead of a generic 500).
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
