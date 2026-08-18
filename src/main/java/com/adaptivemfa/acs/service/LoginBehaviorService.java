package com.adaptivemfa.acs.service;

import org.springframework.stereotype.Service;

@Service
public class LoginBehaviorService {

    public boolean isUnusualLoginTime(int loginHour){
        return loginHour <8 || loginHour >22;
    }

}
