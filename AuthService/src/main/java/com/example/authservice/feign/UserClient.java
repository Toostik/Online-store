package com.example.authservice.feign;

import com.example.authservice.dto.user.UserDto;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getUser(@PathVariable Long id);
    @GetMapping("/api/users/{email}")
    UserDto getUserByEmail(@PathVariable String email);
    @PostMapping("/api/users")
    UserDto createUser(@RequestBody RegisterRequest request);
    @PostMapping("/api/users/authenticate")
    UserDto authenticate(@RequestBody LoginRequest request);
}
