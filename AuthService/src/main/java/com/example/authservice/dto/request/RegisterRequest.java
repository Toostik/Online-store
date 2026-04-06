package com.example.authservice.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private final String email;
    private final String username;
    private final String password;

}
