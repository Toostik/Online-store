package com.example.authservice.feign;

import com.example.authservice.dto.user.UserDto;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
    @GetMapping("/api/v1/users/{id}")
    UserDto getUser(@PathVariable Long id);
    @GetMapping("/api/v1/users")
    UserDto getUserByEmail(@RequestParam String email);
    @PostMapping("/api/v1/users")
    UserDto createUser(@RequestBody RegisterRequest request);
    @PostMapping("/api/v1/users/authenticate")
    UserDto authenticate(@RequestBody LoginRequest request);
}
