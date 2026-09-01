package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AccountSecurity;
import com.adaptivemfa.acs.model.ChangePasswordRequest;
import com.adaptivemfa.acs.model.User;
import com.adaptivemfa.acs.repository.AccountSecurityRepository;
import com.adaptivemfa.acs.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final AccountSecurityRepository accountSecurityRepository;
  private final AccountSecurityService accountSecurityService;
  private final MFAService mfaService;
  public UserService(UserRepository userRepository, PasswordService passwordService, AccountSecurityRepository accountSecurityRepository,  AccountSecurityService accountSecurityService, MFAService mfaService) {
      this.userRepository = userRepository;
      this.passwordService = passwordService;
      this.accountSecurityRepository = accountSecurityRepository;
      this.mfaService = mfaService;
      this.accountSecurityService = accountSecurityService;
  }

  @Transactional
  public void createUser(String username,String password){
      User user = new User();
      user.setUsername(username);
      user.setPassword(passwordService.hashPassword(password));
      user.setRole("USER");
      userRepository.save(user);

      AccountSecurity security = new AccountSecurity();
      security.setusername(username);
      security.setfailedAttempts(0);
      security.setlocked(false);
      security.setlockedAt(null);
      accountSecurityRepository.save(security);
  }

  public boolean authenticate(String username, String password){
      return userRepository.findById(username)
              .map(user ->
                            passwordService.matches(password,
                                                    user.getPassword()
                            )
              )
              .orElse(false);
  }

  public boolean userExists(String username){
      return userRepository.existsById(username);
  }

  public void changePassword(
          String username,
          String currentPassword,
          String newPassword
  ){
      User user = userRepository.findById(username)
              .orElseThrow(()->
                      new RuntimeException("User not found"));
      if(!passwordService.matches(currentPassword,user.getPassword())){
          throw new RuntimeException("Current password is incorrect");
      }
      user.setPassword(passwordService.hashPassword(newPassword));
      userRepository.save(user);
      accountSecurityService.resetFailedAttempts(username);
  }

  public String generatePasswordResetOtp(String username){
      userRepository.findById(username)
              .orElseThrow(()->
                      new RuntimeException("User not found"));
      return mfaService.generateOtp(username);
  }

  public void resetPassword(
          String username,
          String otp,
          String newPassword
  ){
      boolean verified= mfaService.verifyOTP(username, otp);
      if(!verified){
          throw new RuntimeException("Invalid OTP or expired OTP");
      }

      User user= userRepository.findById(username)
              .orElseThrow(()->
                      new RuntimeException("User not found"));

      user.setPassword(passwordService.hashPassword(newPassword));
      userRepository.save(user);
      accountSecurityService.resetFailedAttempts(username);


  }

  @Transactional
    public void createAdmin(String username, String password){
      if(userRepository.existsById(username)){
          throw new RuntimeException("Username already exists");
      }
      User user = new User();
      user.setUsername(username);
      user.setPassword(passwordService.hashPassword(password));
      user.setRole("ADMIN");

      userRepository.save(user);

      AccountSecurity security = new AccountSecurity();

      security.setusername(username);
      security.setfailedAttempts(0);
      security.setlocked(false);
      security.setlockedAt(null);
      accountSecurityRepository.save(security);
  }
}
