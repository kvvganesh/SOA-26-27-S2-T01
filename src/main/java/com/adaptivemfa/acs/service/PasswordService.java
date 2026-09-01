package com.adaptivemfa.acs.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

    public String hashPassword(String password){
        return passwordEncoder.encode(password);
    }
    public boolean matches(String password, String hashedPassword){
        return passwordEncoder.matches(password, hashedPassword);
    }
}
