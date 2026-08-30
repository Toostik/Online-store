package com.example.userservice.service.user.builder;

import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.entity.enums.Role;
import com.example.userservice.entity.enums.UserSecurityStatus;
import com.example.userservice.entity.user.User;
import com.example.userservice.entity.user.UserSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBuilder {

    private final PasswordEncoder passwordEncoder;

    public User create(RegisterRequest request){

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);
        UserSecurity security = new UserSecurity();
        security.setUser(user);
        security.setStatus(UserSecurityStatus.ACTIVE);
        user.setSecurity(security);

        return user;

    }

}
