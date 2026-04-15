package com.example.orderservice.service;

import com.example.orderservice.dto.UserDto;
import com.example.orderservice.feign.ProductClient;
import com.example.orderservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserClient userClient;

    UserDto getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthorized");
        }

        Long id = Long.parseLong(auth.getName());

        String key = "user:" + id;
        UserDto cachedUser = (UserDto) redisTemplate.opsForValue().get(key);

        if(cachedUser!=null){
            return cachedUser;
        }else {
            UserDto user = userClient.getUserById(id);
            if(user==null){
                throw new RuntimeException("User not found");
            }
            redisTemplate.opsForValue().set(key, user, Duration.ofDays(1));
           return user;
        }

    }
}
