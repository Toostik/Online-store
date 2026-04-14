package com.example.authservice.feign;

import com.example.authservice.dto.UserDto;
import com.example.authservice.dto.request.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "http://user-service:8082")
public interface UserClient {
    @GetMapping("/api/users/id/{id}")
    UserDto getUser(@PathVariable Long id);
    @GetMapping("/api/users/email/{email}")
    UserDto getUserByEmail(@PathVariable String email);
    @PostMapping("/api/users/create")
    UserDto createUser(RegisterRequest request);

}
