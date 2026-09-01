package com.adaptivemfa.acs.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
