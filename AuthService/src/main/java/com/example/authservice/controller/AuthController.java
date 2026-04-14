package com.example.authservice.controller;

import com.example.authservice.dto.UserDto;
import com.example.authservice.dto.request.AuthResponse;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RefreshRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        UserDto user = userService.createUser(request);

        return ResponseEntity
                .ok(jwtService.generateTokens(user.getId().toString(),
                        List.of(user.getRole())));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        UserDto user = userService.getUserByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("Wrong password");
        }

        return ResponseEntity.ok(jwtService.generateTokens(user.getId().toString(),
                List.of(user.getRole())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(jwtService.refresh(refreshRequest.getRefreshToken()));
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshRequest refreshRequest) {
        jwtService.logout(refreshRequest.getRefreshToken());
    }

}
