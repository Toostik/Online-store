package com.example.authservice.dto.request;

import lombok.Data;

@Data
public class RegisterResponse {
    private final Long id;
    private final String role;
}
