package com.example.authservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrentUserDto {
    private String email;
    private String username;

    @JsonCreator
    public CurrentUserDto(@JsonProperty("email")String email, @JsonProperty("username") String username) {
        this.email = email;
        this.username = username;
    }
}
