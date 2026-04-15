package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String password;
    private String role;

    @JsonCreator
    public UserDto(@JsonProperty("id") Long id,
                   @JsonProperty("email")String email,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("role")String role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
