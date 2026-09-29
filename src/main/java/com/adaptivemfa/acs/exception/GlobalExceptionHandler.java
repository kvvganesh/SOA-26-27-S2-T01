package com.adaptivemfa.acs.exception;

import com.adaptivemfa.acs.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * -----------------------------------------
     * 1. Validation errors
     * -----------------------------------------
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        var errors =
                ex.getBindingResult().getFieldErrors();

        StringBuilder message =
                new StringBuilder();

        for (var error : errors) {

            message.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "400",
                        "Bad Request",
                        message.toString(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    /*
     * -----------------------------------------
     * 2. User already exists
     * -----------------------------------------
     */

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse>
    handleUserAlreadyExistsException(
            UserAlreadyExistsException ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "409",
                        "Conflict",
                        ex.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }


    /*
     * -----------------------------------------
     * 3. MFA errors
     * -----------------------------------------
     */

    @ExceptionHandler(MFAException.class)
    public ResponseEntity<ErrorResponse>
    handleMFAException(MFAException ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "403",
                        "MFA Failed",
                        ex.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }


    /*
     * -----------------------------------------
     * 4. Unexpected exceptions
     * -----------------------------------------
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleException(Exception ex) {

        /*
         * Keep the real exception on the server.
         * Do NOT expose it to the client.
         */
        ex.printStackTrace();

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "500",
                        "Internal Server Error",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /// Account Lock Exception Handler

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleAccountLockedException(
            AccountLockedException ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "423",
                        "Account Locked",
                        "Account is temporarily locked. Please try again later.",
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(423)
                .body(errorResponse);
    }


    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "429",
                        "Too Many Requests",
                        ex.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(errorResponse);
    }



}

