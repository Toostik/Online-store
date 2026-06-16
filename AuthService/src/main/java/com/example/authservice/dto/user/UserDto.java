package com.example.authservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String role;
    private String eventId;
    private String avatarImagePath;
    private BigDecimal balance;
    private String phone;
    private LocalDateTime createdAt;

}
