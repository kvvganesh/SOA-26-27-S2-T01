package com.adaptivemfa.acs.model;

public class AuthenticationResponse {
    private String status;
    private String risklevel;
    private String message;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRiskLevel() {
        return risklevel;
    }
    public void setRiskLevel(String risklevel) {
        this.risklevel = risklevel;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
