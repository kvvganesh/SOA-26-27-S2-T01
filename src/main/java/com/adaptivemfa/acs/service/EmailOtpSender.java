package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.exception.ApiException;
import com.adaptivemfa.acs.model.User;
import com.adaptivemfa.acs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends the OTP to the e-mail address stored on the user's account.
 *
 * Active whenever the "dev" profile is NOT on. SMTP settings live in
 * application.properties (spring.mail.*) and are read from environment
 * variables, so no password is ever committed to the repository.
 */
@Service
@Profile("!dev")
public class EmailOtpSender implements OtpSender {

    private final JavaMailSender mailSender;

    private final UserRepository userRepository;

    private final String from;


    public EmailOtpSender(
            JavaMailSender mailSender,
            UserRepository userRepository,
            @Value("${spring.mail.username:}") String from) {

        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.from = from;
    }


    @Override
    public void send(
            String username,
            String otp,
            OtpPurpose purpose) {

        String email = findEmail(username);

        if (email == null) {

            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "No email address is registered for this account, "
                            + "so a code cannot be sent."
            );
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        if (from != null && !from.isBlank()) {

            message.setFrom(from);
        }

        message.setTo(email);

        message.setSubject(
                purpose == OtpPurpose.LOGIN
                        ? "Your sign-in code"
                        : "Your password reset code"
        );

        message.setText(
                "Your one-time code is: " + otp
                        + "\n\nIt expires in 2 minutes and can be used once."
                        + "\n\nIf this wasn't you, ignore this email and "
                        + "consider changing your password."
                        + "\nNever share this code with anyone."
        );

        try {

            mailSender.send(message);

        } catch (MailException exception) {

            // Log the technical reason for the developer only.
            System.err.println(
                    "OTP e-mail could not be sent: "
                            + exception.getMessage()
            );

            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "We couldn't send the code right now. "
                            + "Please try again in a moment."
            );
        }
    }


    @Override
    public String maskedDestination(String username) {

        String email = findEmail(username);

        return email == null
                ? "your registered email"
                : maskEmail(email);
    }


    private String findEmail(String username) {

        String email =
                userRepository.findById(username)
                        .map(User::getEmail)
                        .orElse(null);

        return (email == null || email.isBlank())
                ? null
                : email.trim();
    }


    /** "ganesh@gmail.com" -> "g***@gmail.com" */
    public static String maskEmail(String email) {

        int at = email.indexOf('@');

        if (at <= 1) {

            return at < 0 ? "***" : "***" + email.substring(at);
        }

        return email.charAt(0) + "***" + email.substring(at);
    }
}
