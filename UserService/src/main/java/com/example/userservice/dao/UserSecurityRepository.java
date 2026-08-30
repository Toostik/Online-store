package com.example.userservice.dao;

import com.example.userservice.entity.user.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {
}
