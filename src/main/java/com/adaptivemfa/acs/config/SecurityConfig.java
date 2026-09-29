package com.adaptivemfa.acs.config;

import com.adaptivemfa.acs.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;


    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService) {

        this.customUserDetailsService =
                customUserDetailsService;
    }


    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==========================================
    // AUTHENTICATION PROVIDER
    // ==========================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // ==========================================
    // CORS CONFIGURATION
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );


        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );


        configuration.setAllowedHeaders(
                List.of(
                        "Content-Type",
                        "Authorization"
                )
        );


        configuration.setAllowCredentials(
                true
        );


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }


    // ==========================================
    // SECURITY FILTER CHAIN
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http


                // ==================================
                // CORS
                // ==================================

                .cors(cors -> {})


                // ==================================
                // CSRF
                // ==================================
                //
                // Currently disabled because this
                // project uses a custom frontend
                // authentication flow.
                //
                // We can introduce CSRF protection
                // later when the final session
                // architecture is complete.
                //

                .csrf(csrf -> csrf.disable())


                // ==================================
                // AUTHORIZATION
                // ==================================

                .authorizeHttpRequests(auth -> auth


                        // ------------------------------
                        // PUBLIC AUTHENTICATION APIs
                        // ------------------------------

                        .requestMatchers(
                                "/auth/login",
                                "/auth/verify-mfa",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/users/register",
                                "/security/enroll"
                        )
                        .permitAll()


                        // ------------------------------
                        // CORS PREFLIGHT
                        // ------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()


                        // ------------------------------
                        // CHANGE PASSWORD
                        // ------------------------------

                        .requestMatchers(
                                "/auth/change-password"
                        )
                        .authenticated()


                        // ------------------------------
                        // ADMIN APIs
                        // ------------------------------

                        .requestMatchers(
                                "/admin/**",
                                "/audit/**"
                        )
                        .hasRole("ADMIN")


                        // ------------------------------
                        // ALL OTHER APIs
                        // ------------------------------

                        .anyRequest()
                        .authenticated()
                )


                // ==================================
                // FORM LOGIN
                // ==================================
                //
                // We are NOT using Spring's default
                // login page.
                //
                // Login is handled by our own
                // /auth/login endpoint.
                //

                .formLogin(
                        form -> form.disable()
                )


                // ==================================
                // HTTP BASIC
                // ==================================
                //
                // Removed.
                //
                // Authentication will now be maintained
                // through the HTTP session that we create
                // after successful custom authentication.
                //

                .httpBasic(
                        httpBasic -> httpBasic.disable()
                );


        return http.build();
    }
}