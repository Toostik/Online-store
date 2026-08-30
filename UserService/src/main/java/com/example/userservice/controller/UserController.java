package com.example.userservice.controller;

import com.example.userservice.dto.user.UserDto;
import com.example.userservice.dto.user.UserProfile;
import com.example.userservice.dto.request.LoginRequest;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.service.user.UserQueryService;
import com.example.userservice.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserQueryService userQueryService;

    //Current user
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        log.info("USER_GET_CURRENT request");
        return ResponseEntity.ok(userQueryService.getCurrentUser());
    }

    @GetMapping("/me/profile")
    public ResponseEntity<UserProfile> getProfileCurrentUser() {
        log.info("USER_GET_PROFILE request");
        return ResponseEntity.ok(userQueryService.getProfile());
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatarImage(@RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadImages(file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/balance")
    public ResponseEntity<?> addBalance(@RequestParam("balance") BigDecimal balance) {
        userService.addBalance(balance);
        return ResponseEntity.ok().build();
    }

    //Get user
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId) {
        log.info("USER_GET_BY_ID request id={}", userId);
        return ResponseEntity.ok(userQueryService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserByEmail(
            @RequestParam String email) {

        log.info("USER_GET_BY_EMAIL request email={}", email);
        return ResponseEntity.ok(userQueryService.getUserByEmail(email));

    }

    //Common
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterRequest request) {
        log.info("USER_CREATE request email={}", request.getEmail());
        return ResponseEntity.ok(userService.createUser(request));
    }


    //Internal
    @PostMapping("/authenticate")
    public ResponseEntity<UserDto> authenticate(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }



}
