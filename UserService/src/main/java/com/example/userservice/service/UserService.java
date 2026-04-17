package com.example.userservice.service;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.kafka.KafkaJsonProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaJsonProducer kafkaJsonProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final ObjectMapper objectMapper;

    public UserDto createUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        kafkaJsonProducer.sendMessage(user.toDto());
        return user.toDto();
    }

    @Cacheable(value = "user", key = "#userId", unless = "#result == null")
    public UserDto getUserById(Long userId) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.toDto();
    }

    public UserDto getUserByEmail(String email) {
        Long userId = userRepository.findIdByEmail(email);
        if (userId == null) {
            throw new RuntimeException("User not found!");
        }
        String key = "user:" + userId;
        Object cachedUser = redisTemplate.opsForValue().get(key);
        if (cachedUser == null) {
            User user = userRepository.getUserByEmail(email).orElseThrow(
                    () -> new RuntimeException("User not found")
            );
            redisTemplate.opsForValue().set(key, user.toDto(), Duration.ofDays(1));
            return user.toDto();
        }
        return objectMapper.convertValue(cachedUser, UserDto.class);
    }

    public UserDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        String key = "user:" + userId;

        Object cachedUser = redisTemplate.opsForValue().get(key);

        if (cachedUser == null) {

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("User not found!")
            );
            redisTemplate.opsForValue().set(key, user.toDto(), Duration.ofDays(1));
            return user.toDto();

        }
        return objectMapper.convertValue(cachedUser, UserDto.class);
    }
}
