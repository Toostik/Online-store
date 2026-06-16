package com.example.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Email(message = "The mail is incorrect")
    private String email;
    @NotBlank(message = "The phone must be filled in")
    private String phone;
    @NotBlank(message = "The name is incorrect")
    private String username;
    @NotBlank(message = "The password is incorrect")
    private String password;
}
