package com.example.userservice.dto.request;

import com.example.userservice.entity.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private final String email;
    private final String username;
    private final String password;

    public User toUser() {
        return new User(
                email,
                username
        );
    }
}
