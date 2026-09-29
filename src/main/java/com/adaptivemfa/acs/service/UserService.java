package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AccountSecurity;
import com.adaptivemfa.acs.model.User;
import com.adaptivemfa.acs.repository.AccountSecurityRepository;
import com.adaptivemfa.acs.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final AccountSecurityRepository accountSecurityRepository;
    private final AccountSecurityService accountSecurityService;
    private final MFAService mfaService;
    private final PasswordPolicyService passwordPolicyService;


    public UserService(
            UserRepository userRepository,
            PasswordService passwordService,
            AccountSecurityRepository accountSecurityRepository,
            AccountSecurityService accountSecurityService,
            MFAService mfaService,
            PasswordPolicyService passwordPolicyService) {

        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.accountSecurityRepository = accountSecurityRepository;
        this.accountSecurityService = accountSecurityService;
        this.mfaService = mfaService;
        this.passwordPolicyService = passwordPolicyService;
    }


    /*
     * =========================================
     * CREATE USER
     * =========================================
     */

    @Transactional
    public void createUser(
            String username,
            String password) {

        if (userRepository.existsById(username)) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        // Validate password before hashing
        passwordPolicyService.validatePassword(password);

        User user = new User();

        user.setUsername(username);

        user.setPassword(
                passwordService.hashPassword(password)
        );

        user.setRole("USER");

        userRepository.save(user);


        AccountSecurity security =
                new AccountSecurity();

        security.setusername(username);
        security.setfailedAttempts(0);
        security.setlocked(false);
        security.setlockedAt(null);

        accountSecurityRepository.save(security);
    }


    /*
     * =========================================
     * AUTHENTICATE
     * =========================================
     */

    public boolean authenticate(
            String username,
            String password) {

        return userRepository.findById(username)
                .map(user ->
                        passwordService.matches(
                                password,
                                user.getPassword()
                        )
                )
                .orElse(false);
    }


    /*
     * =========================================
     * CHECK USER EXISTS
     * =========================================
     */

    public boolean userExists(String username) {

        return userRepository.existsById(username);
    }


    /*
     * =========================================
     * CHANGE PASSWORD
     * =========================================
     */

    public void changePassword(
            String username,
            String currentPassword,
            String newPassword) {

        User user =
                userRepository.findById(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        if (!passwordService.matches(
                currentPassword,
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }


        // Validate new password before hashing
        passwordPolicyService.validatePassword(
                newPassword
        );


        user.setPassword(
                passwordService.hashPassword(
                        newPassword
                )
        );

        userRepository.save(user);

        accountSecurityService.resetFailedAttempts(
                username
        );
    }


    /*
     * =========================================
     * GENERATE PASSWORD RESET OTP
     * =========================================
     */


    public void generatePasswordResetOtp(String username) {

        if (userRepository.existsById(username)) {
            mfaService.generatePasswordResetOtp(username);

        }

    }




    /*
     * =========================================
     * RESET PASSWORD
     * =========================================
     */

    public void resetPassword(
            String username,
            String otp,
            String newPassword) {


        boolean verified =
                mfaService.verifyPasswordResetOtp(
                        username,
                        otp
                );


        if (!verified) {

            throw new RuntimeException(
                    "Invalid OTP or expired OTP"
            );
        }


        User user =
                userRepository.findById(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // Validate new password before hashing
        passwordPolicyService.validatePassword(
                newPassword
        );


        user.setPassword(
                passwordService.hashPassword(
                        newPassword
                )
        );

        userRepository.save(user);

        accountSecurityService.resetFailedAttempts(
                username
        );
    }


    /*
     * =========================================
     * CREATE ADMIN
     * =========================================
     */

    @Transactional
    public void createAdmin(
            String username,
            String password) {

        if (userRepository.existsById(username)) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }


        // Validate admin password too
        passwordPolicyService.validatePassword(
                password
        );


        User user = new User();

        user.setUsername(username);

        user.setPassword(
                passwordService.hashPassword(
                        password
                )
        );

        user.setRole("ADMIN");

        userRepository.save(user);


        AccountSecurity security =
                new AccountSecurity();

        security.setusername(username);
        security.setfailedAttempts(0);
        security.setlocked(false);
        security.setlockedAt(null);

        accountSecurityRepository.save(security);
    }
}

