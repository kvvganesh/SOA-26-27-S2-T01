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

        return account.get().islocked();
    }


    // Record a failed login attempt
    public void recordFailedAttempt(String username) {

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


        // Lock account after 5 failed attempts
        if (attempts >= 5) {

            security.setlocked(true);
            security.setlockedAt(LocalDateTime.now());
        }


        accountSecurityRepository.save(security);
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
}