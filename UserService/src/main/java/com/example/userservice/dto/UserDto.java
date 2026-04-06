package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String password;
    private String role;
    @JsonCreator
    public UserDto(@JsonProperty("email") String email,@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
