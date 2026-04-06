package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String password;
    private String role;

    public UserDto(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
