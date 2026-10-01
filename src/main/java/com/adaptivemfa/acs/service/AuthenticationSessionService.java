package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationSessionService {

    private final CustomUserDetailsService userDetailsService;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();


    public AuthenticationSessionService(
            CustomUserDetailsService userDetailsService) {

        this.userDetailsService =
                userDetailsService;
    }


    // ==========================================
    // CREATE AUTHENTICATED SESSION
    // ==========================================

    public void createAuthenticatedSession(
            String username,
            HttpServletRequest request,
            HttpServletResponse response) {


        // ------------------------------------------
        // Session fixation protection: give any
        // pre-existing session a fresh id
        // ------------------------------------------

        if (request.getSession(false) != null) {

            request.changeSessionId();
        }


        // ------------------------------------------
        // Load user from database
        // ------------------------------------------

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        username
                );


        // ------------------------------------------
        // Create Spring Security Authentication
        // ------------------------------------------

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


        // ------------------------------------------
        // Create Security Context
        // ------------------------------------------

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();


        securityContext.setAuthentication(
                authentication
        );


        // ------------------------------------------
        // Put context into current request
        // ------------------------------------------

        SecurityContextHolder.setContext(
                securityContext
        );


        // ------------------------------------------
        // Save context into HTTP session
        // ------------------------------------------

        securityContextRepository.saveContext(
                securityContext,
                request,
                response
        );
    }


    // ==========================================
    // LOGOUT / CLEAR SESSION
    // ==========================================

    public void clearSession(
            HttpServletRequest request,
            HttpServletResponse response) {

        SecurityContextHolder.clearContext();

        securityContextRepository.saveContext(
                SecurityContextHolder.createEmptyContext(),
                request,
                response
        );
    }
}