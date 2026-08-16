package com.adaptivemfa.acs.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import com.adaptivemfa.acs.model.User;
import com.adaptivemfa.acs.service.UserService;






@RestController
public class HelloController {

    private final UserService userService;
    public HelloController(UserService userService) {
        this.userService=userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Adaptive MFA System!";
    }

    @PostMapping("/users")
    public User CreateUser(@RequestBody User user){
       return userService.processUser(user);
    }


}


