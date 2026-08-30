package com.example.authservice.controller;

import com.example.authservice.dto.response.AuthResponse;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RefreshRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.service.jwt.JwtService;
import com.example.authservice.service.integration.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("AUTH_REGISTER request email={}", request.getEmail());
        return ResponseEntity
                .ok(jwtService.register(request));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("AUTH_LOGIN request email={}", request.getEmail());
        return ResponseEntity.ok(jwtService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        log.info("AUTH_REFRESH token received");
        return ResponseEntity.ok(jwtService.refresh(refreshRequest.getRefreshToken()));
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshRequest refreshRequest) {
        log.info("AUTH_LOGOUT request received");
        jwtService.logout(refreshRequest.getRefreshToken());
    }

}
