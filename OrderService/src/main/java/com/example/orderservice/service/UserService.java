package com.example.orderservice.service;

import com.example.orderservice.feign.ProductClient;
import com.example.orderservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserClient userClient;

    boolean isUserExist(Long id){
        String key = "user:" + id;
        if(redisTemplate.opsForValue().get(key)!=null){
            return true;
        }else {
           boolean userExist = userClient.isUserExist(id);
           if(userExist) redisTemplate.opsForValue().set(key, id, Duration.ofDays(1));
           return userExist;
        }
    }
}
