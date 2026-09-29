package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AccountSecurity;
import com.adaptivemfa.acs.repository.AccountSecurityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountSecurityService {

    private final AccountSecurityRepository accountSecurityRepository;

    public AccountSecurityService(
            AccountSecurityRepository accountSecurityRepository) {

        this.accountSecurityRepository = accountSecurityRepository;
    }

    // Check whether the account is locked
    public boolean isAccountLocked(String username) {

        Optional<AccountSecurity> account =
                accountSecurityRepository.findById(username);

        if (account.isEmpty()) {
            return false;
        }

        AccountSecurity security = account.get();

        // Account is not locked
        if (!security.islocked()) {
            return false;
        }

        // Account is locked — check when it was locked
        LocalDateTime lockedAt = security.getlockedAt();

        if (lockedAt != null &&
                LocalDateTime.now().isAfter(lockedAt.plusHours(12))) {

            // Automatically unlock after 12 hours
            security.setlocked(false);
            security.setfailedAttempts(0);
            security.setlockedAt(null);

            accountSecurityRepository.save(security);

            return false;
        }

        // Still within the 12-hour lock period
        return true;
    }


    // Record a failed login attempt
    public int recordFailedAttempt(String username) {

        Optional<AccountSecurity> account =
                accountSecurityRepository.findById(username);

        AccountSecurity security;

        if (account.isPresent()) {

            security = account.get();

        } else {

            security = new AccountSecurity();

            security.setusername(username);
            security.setfailedAttempts(0);
            security.setlocked(false);
        }

        int attempts = security.getfailedAttempts() + 1;

        security.setfailedAttempts(attempts);


        // Lock account after 6 failed attempts
        if (attempts >= 6) {

            security.setlocked(true);
            security.setlockedAt(LocalDateTime.now());
        }


        accountSecurityRepository.save(security);
        return attempts;
    }


    // Reset failed attempts after successful authentication
    public void resetFailedAttempts(String username) {

        Optional<AccountSecurity> account =
                accountSecurityRepository.findById(username);

        if (account.isPresent()) {

            AccountSecurity security = account.get();

            security.setfailedAttempts(0);
            security.setlocked(false);
            security.setlockedAt(null);

            accountSecurityRepository.save(security);
        }
    }

    public void unlockAccount(String username) {
        Optional<AccountSecurity> account =
                accountSecurityRepository.findById(username);

        if(account.isEmpty()){
            throw new RuntimeException("Account doesn't exist");

        }
        AccountSecurity security = account.get();

        security.setfailedAttempts(0);
        security.setlocked(false);
        security.setlockedAt(null);

        accountSecurityRepository.save(security);
    }
}