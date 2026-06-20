package com.example.userservice.dto.user;

import com.example.userservice.dto.user.address.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String role;
    private String avatarImagePath;
    private BigDecimal balance;
    private String phone;
    private LocalDateTime createdAt;
    private List<AddressDto> addresses;
    private String securityStatus;
}
