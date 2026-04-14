package com.example.authservice.service;

import com.example.authservice.dto.UserDto;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    public List<String> getRoles(Long id) {
        UserDto user = userClient.getUser(id);
        return List.of(user.getRole());
    }

    public UserDto createUser(RegisterRequest request) {
        return userClient.createUser(request);
    }

    public UserDto getUserByEmail(String email) {
        return userClient.getUserByEmail(email);
    }

}
