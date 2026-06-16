package com.example.userservice.dto.user;

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

    public UserDto(Long id,
                   String email,
                   String username,
                   String phone,
                   LocalDateTime createdAt,
                   String role,
                   String avatarImagePath,
                   BigDecimal balance
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.createdAt = createdAt;
        this.role = role;
        this.avatarImagePath = avatarImagePath;
        this.balance = balance;
    }
}
