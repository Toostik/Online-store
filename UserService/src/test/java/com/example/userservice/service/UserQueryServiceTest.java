package com.example.userservice.service;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.user.UserDto;
import com.example.userservice.entity.enums.Role;
import com.example.userservice.entity.user.User;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.service.security.SecurityService;
import com.example.userservice.service.user.UserQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOps;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    void getUserById_shouldReturnUserDto(){
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEmail("test@mail.ru");
        user.setUsername("test");
        user.setRole(Role.USER);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userQueryService.getUserById(userId);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getRole().name(), userDto.getRole());

    }
    @Test
    void getUserById_shouldReturnException_whenUserNotFound(){
        Long userId = 1L;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userQueryService.getUserById(userId));

    }

    @Test
    void getUserById_shouldReturnUserDto_whenRedisHasUser(){
        Long userId = 1L;
        String key = "user:" + userId;

        UserDto cachedDto = new UserDto();
        cachedDto.setId(userId);
        cachedDto.setEmail("test@mail.ru");
        cachedDto.setUsername("test");
        cachedDto.setRole("USER");

        Object cachedUser = cachedDto;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(cachedUser);

        UserDto result = userQueryService.getUserById(userId);

        assertEquals(cachedDto.getId(), result.getId());
        assertEquals(cachedDto.getEmail(), result.getEmail());

        verify(userRepository, never()).getUserById(any());
    }



}
