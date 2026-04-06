package com.example.userservice.controller;

import com.example.userservice.dto.CurrentUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.dto.request.RegisterResponse;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDto> getCurrentUser(@RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(userService.getCurrentUser(userId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CurrentUserDto> getUser(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userService.getCurrentUser(userId));
    }
    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUser(@RequestParam String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    @PostMapping("/create")
    public ResponseEntity<RegisterResponse> createUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }
}
