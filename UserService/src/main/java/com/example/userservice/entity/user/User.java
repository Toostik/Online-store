package com.example.userservice.entity.user;

import com.example.userservice.dto.user.UserDto;
import com.example.userservice.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "avatar_image", unique = true)
    private String avatarImagePath;
    @Column(name = "balance")
    private BigDecimal balance = new BigDecimal("0");

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserImage> images;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSecurity security;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAddress> addresses;

    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public UserDto toDto() {
        return new UserDto(
                id,
                email,
                username,
                phone,
                createdAt,
                role.name(),
                avatarImagePath,
                balance
        );
    }

}
