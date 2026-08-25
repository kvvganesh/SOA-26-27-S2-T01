package com.adaptivemfa.acs.model;

public class AuditStatistics {

    private long totalLogs;
    private long successfulLogins;
    private long mfaRequired;
    private long accessDenied;
    private long mfaAuthenticated;
    private long mfaFailed;
    private long highRiskAttempts;
    private long mediumRiskAttempts;

    public long getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(long totalLogs) {
        this.totalLogs = totalLogs;
    }

    public long getSuccessfulLogins() {
        return successfulLogins;
    }

    public void setSuccessfulLogins(long successfulLogins) {
        this.successfulLogins = successfulLogins;
    }

    public long getMfaRequired() {
        return mfaRequired;
    }

    public void setMfaRequired(long mfaRequired) {
        this.mfaRequired = mfaRequired;
    }

    public long getAccessDenied() {
        return accessDenied;
    }

    public void setAccessDenied(long accessDenied) {
        this.accessDenied = accessDenied;
    }

    public long getMfaAuthenticated() {
        return mfaAuthenticated;
    }

    public void setMfaAuthenticated(long mfaAuthenticated) {
        this.mfaAuthenticated = mfaAuthenticated;
    }

    public long getMfaFailed() {
        return mfaFailed;
    }

    public void setMfaFailed(long mfaFailed) {
        this.mfaFailed = mfaFailed;
    }

    public long getHighRiskAttempts() {
        return highRiskAttempts;
    }

    public void setHighRiskAttempts(long highRiskAttempts) {
        this.highRiskAttempts = highRiskAttempts;
    }

    public long getMediumRiskAttempts() {
        return mediumRiskAttempts;
    }

    public void setMediumRiskAttempts(long mediumRiskAttempts) {
        this.mediumRiskAttempts = mediumRiskAttempts;
    }
}