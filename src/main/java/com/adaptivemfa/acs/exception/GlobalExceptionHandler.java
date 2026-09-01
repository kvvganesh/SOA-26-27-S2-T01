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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        var errors=ex.getBindingResult().getFieldErrors();
        StringBuilder message=new StringBuilder();
        for (var error: errors){
            message.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append(";\n");
        }
        ErrorResponse errorResponse = new ErrorResponse(
                "400",
                "Internal Server Error",
                message.toString(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "403",
                "MFA Failed",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "409",
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }
}
