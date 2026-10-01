package com.adaptivemfa.acs.exception;

import com.adaptivemfa.acs.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
     * 3b. Business-rule errors with an explicit
     *     HTTP status (see ApiException)
     * -----------------------------------------
     */

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            ApiException ex) {

        HttpStatus status = ex.getStatus();

        ErrorResponse errorResponse =
                new ErrorResponse(
                        String.valueOf(status.value()),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }


    /*
     * -----------------------------------------
     * 3c. Malformed requests / unknown routes.
     *     Without these the catch-all below
     *     would report them as HTTP 500.
     * -----------------------------------------
     */

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            Exception ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "400",
                        "Bad Request",
                        "The request body or parameters are invalid.",
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoResourceFoundException ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "404",
                        "Not Found",
                        "The requested resource was not found.",
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "405",
                        "Method Not Allowed",
                        "This HTTP method is not supported for the endpoint.",
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
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

