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
  public UserService(UserRepository userRepository, PasswordService passwordService, AccountSecurityRepository accountSecurityRepository,  AccountSecurityService accountSecurityService) {
      this.userRepository = userRepository;
      this.passwordService = passwordService;
      this.accountSecurityRepository = accountSecurityRepository;
      this.accountSecurityService = accountSecurityService;
  }

  @Transactional
  public void createUser(String username,String password){
      User user = new User();
      user.setUsername(username);
      user.setPassword(passwordService.hashPassword(password));
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
}
