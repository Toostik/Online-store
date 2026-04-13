package com.example.userservice.dto.request;

import com.example.userservice.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class RegisterResponse {
    private final Long id;
    private final List<String> roles;


}
