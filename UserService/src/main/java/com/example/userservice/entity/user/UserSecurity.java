package com.example.userservice.entity.user;

import com.example.userservice.entity.enums.UserSecurityStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_security")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserSecurityStatus status = UserSecurityStatus.ACTIVE;

}
