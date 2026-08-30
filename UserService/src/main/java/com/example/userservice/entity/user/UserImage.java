package com.example.userservice.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "image_path", unique = true)
    private String imagePath;

    @ManyToOne
    private User user;

}
