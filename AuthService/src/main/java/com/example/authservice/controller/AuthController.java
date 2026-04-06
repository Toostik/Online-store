package com.example.authservice.controller;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(jwtService.createToken(request));
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(jwtService.refreshToken(request));
    }


}
