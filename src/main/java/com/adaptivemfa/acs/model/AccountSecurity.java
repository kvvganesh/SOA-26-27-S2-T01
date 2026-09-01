package com.adaptivemfa.acs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity

public class AccountSecurity {
    @Id
    private String username;
    private int failedAttempts;
    boolean locked;
    private LocalDateTime lockedAt;

    public void setusername(String username) {
        this.username = username;
    }
    public String getusername() {
        return username;
    }
    public void setfailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
    public int getfailedAttempts() {
        return failedAttempts;
    }
    public void setlocked(boolean locked) {
        this.locked = locked;
    }
    public boolean islocked() {
        return locked;
    }
    public void setlockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }
    public LocalDateTime getlockedAt() {
        return lockedAt;
    }

}
