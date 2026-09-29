package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class LoginBehaviorService {
    private final TrustedLoginTimeService trustedLoginTimeService;
    public LoginBehaviorService(TrustedLoginTimeService trustedLoginTimeService) {
        this.trustedLoginTimeService = trustedLoginTimeService;
    }

    public boolean isUnusualLoginTime(String username, int loginHour){
      boolean trusted=trustedLoginTimeService.isTrustedTime(username, loginHour);
      return !trusted;
    }

}
