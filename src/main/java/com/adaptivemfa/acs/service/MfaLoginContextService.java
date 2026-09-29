package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.MfaLoginContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class MfaLoginContextService {

    private final ConcurrentHashMap<
            String,
            MfaLoginContext
            > contexts =
            new ConcurrentHashMap<>();


    // ==========================================
    // STORE MFA LOGIN CONTEXT
    // ==========================================

    public void store(
            String username,
            MfaLoginContext context) {

        contexts.put(
                username,
                context
        );
    }


    // ==========================================
    // GET MFA LOGIN CONTEXT
    // ==========================================

    public MfaLoginContext get(
            String username) {

        return contexts.get(username);
    }


    // ==========================================
    // REMOVE MFA LOGIN CONTEXT
    // ==========================================

    public void remove(
            String username) {

        contexts.remove(username);
    }
}