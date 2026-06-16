package com.example.authservice.exceptions.user;

public class UserIdOrRoleException extends RuntimeException {
    public UserIdOrRoleException(String message) {
        super(message);
    }
}
