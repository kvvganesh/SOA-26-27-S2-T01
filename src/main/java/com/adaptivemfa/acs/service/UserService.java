package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.exception.ApiException;
import com.adaptivemfa.acs.exception.UserAlreadyExistsException;
import com.adaptivemfa.acs.model.AccountSecurity;
import com.adaptivemfa.acs.model.User;
import com.adaptivemfa.acs.repository.AccountSecurityRepository;
import com.adaptivemfa.acs.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
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
            String password,
            String email) {

        if (userRepository.existsById(username)) {

            throw new UserAlreadyExistsException(
                    "Username already exists!"
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

        user.setEmail(
                email == null ? null : email.trim()
        );

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
                                new ApiException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found"
                                )
                        );


        if (!passwordService.matches(
                currentPassword,
                user.getPassword())) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
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

            /*
             * The API answers "if the account exists, instructions were
             * sent" no matter what. So a delivery problem (no e-mail on
             * file, mail server down) must NOT change the response,
             * otherwise an attacker could tell which usernames exist.
             */
            try {

                mfaService.generatePasswordResetOtp(username);

            } catch (ApiException exception) {

                System.err.println(
                        "Password reset OTP was not delivered for "
                                + username + ": "
                                + exception.getMessage()
                );
            }
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

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid OTP or expired OTP"
            );
        }


        User user =
                userRepository.findById(username)
                        .orElseThrow(() ->
                                new ApiException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid OTP or expired OTP"
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

            throw new UserAlreadyExistsException(
                    "Username already exists!"
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

