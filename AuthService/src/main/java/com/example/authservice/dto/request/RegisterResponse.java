package com.example.authservice.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RegisterResponse {
    private final Long id;
    private final List<String> roles;
}
