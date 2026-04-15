package com.example.orderservice.feign;

import com.example.orderservice.dto.UserDto;
import jakarta.persistence.Lob;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://user-service:8082")
public interface UserClient {
    @GetMapping("/api/users/id/{id}")
    UserDto getUserById(@PathVariable Long id);
}
