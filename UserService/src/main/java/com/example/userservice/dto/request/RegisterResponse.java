package com.example.userservice.dto.request;

import com.example.userservice.entity.Role;
import lombok.Data;

@Data
public class RegisterResponse {
    private final Long id;
    private final String role;


}
