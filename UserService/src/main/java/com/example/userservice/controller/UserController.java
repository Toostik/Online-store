package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/id/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }
}
