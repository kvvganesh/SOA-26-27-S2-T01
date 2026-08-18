package com.adaptivemfa.acs.model;

import java.time.LocalDateTime;

public class AuditLog {
    private LocalDateTime timestamp;
    private String username;
    private String deviceId;
    private String location;
    private int riskScore;
    private String riskLevel;
    private String decision;

    public AuditLog(){

    }
    public AuditLog(
            LocalDateTime timestamp,
            String username,
            String deviceId,
            String location,
            int riskScore,
            String riskLevel,
            String decision
    ){
        this.timestamp = timestamp;
        this.username = username;
        this.deviceId = deviceId;
        this.location = location;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.decision = decision;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
