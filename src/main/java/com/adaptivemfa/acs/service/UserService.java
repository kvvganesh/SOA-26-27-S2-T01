package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User processUser(User user){
        return user;
    }
}
