package com.example.authservice.service;

import com.example.authservice.dto.UserDto;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.request.RegisterResponse;
import com.example.authservice.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;
    private final WebClient userServiceWebClient;
    private final PasswordEncoder passwordEncoder;

    public String createToken(RegisterRequest request) {
       RegisterResponse registerResponse = userServiceWebClient.post()
                .uri("/api/users/create")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RegisterResponse.class).block();

        return jwtUtil.createToken(
                registerResponse.getId().toString(),
                registerResponse.getRoles()
        );
    }

    public String refreshToken(LoginRequest request) {

        UserDto user = userServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/by-email")
                        .queryParam("email", request.getEmail())
                        .build())
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        if(user == null){
            throw new RuntimeException("User is null");
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Password is incorrect");
        }


        return jwtUtil.createToken(
                user.getId().toString(),
                List.of(user.getRole())
        );

    }
}
