package com.adaptivemfa.acs.config;

import com.adaptivemfa.acs.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /*
     * Frontend origins that may call this API with cookies.
     * Override with:  mfa.cors.allowed-origins=http://localhost:3000
     * (5174 is where Vite moves to when 5173 is already in use.)
     */
    private final String[] allowedOrigins;


    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            @Value("${mfa.cors.allowed-origins:http://localhost:5173,http://localhost:5174}")
            String[] allowedOrigins) {

        this.customUserDetailsService =
                customUserDetailsService;

        this.allowedOrigins =
                allowedOrigins;
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
                Arrays.asList(allowedOrigins)
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
                        "Authorization",
                        "Accept"
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
                                "/auth/logout",
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
                // ERROR RESPONSES
                // ==================================
                //
                // Without this an expired / missing
                // session produced an EMPTY HTTP 403,
                // which the frontend could not explain
                // (and could not tell apart from a
                // permission problem).
                //

                .exceptionHandling(handling -> handling

                        .authenticationEntryPoint(
                                (request, response, exception) ->
                                        writeError(
                                                response,
                                                401,
                                                "Unauthorized",
                                                "You are not signed in "
                                                        + "or your session "
                                                        + "has expired. "
                                                        + "Please sign in again."
                                        )
                        )

                        .accessDeniedHandler(
                                (request, response, exception) ->
                                        writeError(
                                                response,
                                                403,
                                                "Forbidden",
                                                "You do not have "
                                                        + "permission to "
                                                        + "perform this action."
                                        )
                        )
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


    // ==========================================
    // JSON ERROR BODY (same shape as ErrorResponse)
    // ==========================================

    private static void writeError(
            HttpServletResponse response,
            int status,
            String error,
            String message) throws IOException {

        response.setStatus(status);

        response.setContentType("application/json");

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        response.getWriter().write(
                "{\"status\":\"" + status + "\","
                        + "\"error\":\"" + error + "\","
                        + "\"message\":\"" + message + "\"}"
        );
    }
}