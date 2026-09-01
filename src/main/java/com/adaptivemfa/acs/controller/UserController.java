package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.exception.UserAlreadyExistsException;
import com.adaptivemfa.acs.model.RegisterRequest;
import com.adaptivemfa.acs.model.RegistrationResponse;
import com.adaptivemfa.acs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        if(userService.userExists(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists!");
        }
        userService.createUser(
                request.getUsername(),
                request.getPassword()
        );
        RegistrationResponse response=new RegistrationResponse(
                request.getUsername(),
                "REGISTERED",
                "User registered successfully",
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
