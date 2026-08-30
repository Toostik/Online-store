package com.example.userservice.service.user.cache;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.user.UserDto;
import com.example.userservice.entity.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, UserDto> redisTemplate;
    private final String USER_PREFIX = "user:";

    public void save(UserDto userDto) {

        String key = USER_PREFIX + userDto.getId();

        try {

            redisTemplate.opsForValue().set(key, userDto, Duration.ofDays(1));

        } catch (Exception e) {
            log.warn("Redis unavailable", e);
        }

    }

    public UserDto get(Long userId) {

        String key = USER_PREFIX + userId;

        try {

            UserDto userDto =
                    redisTemplate.opsForValue().get(key);

            if (userDto != null) return userDto;

        } catch (Exception ex) {

            log.warn(
                    "Redis unavailable",
                    ex
            );
        }

        User user = userRepository.getUserById(userId)
                .orElseThrow();

        save(user.toDto());

        return user.toDto();

    }

    public void remove(Long userId) {

        String key = USER_PREFIX + userId;

        try {

            redisTemplate.delete(key);

        } catch (Exception e) {
            log.warn("Redis unavailable", e);
        }

    }

}
