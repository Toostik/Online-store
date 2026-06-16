package com.example.userservice.service;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.entity.enums.Role;
import com.example.userservice.entity.user.User;
import com.example.userservice.exceptions.UserExistsException;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOps;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveInRepositoryAndSendKafkaMessage(){
        RegisterRequest request = new RegisterRequest();
        String email = "test@mail.ru";
        request.setEmail(email);
        String namePass = "test";
        request.setUsername(namePass);
        request.setPassword(namePass);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();

        verify(kafkaProducer).sendMessage("users-registered", saved.toDto());

        assertEquals(email, saved.getEmail());
        assertEquals(namePass, saved.getUsername());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    void createUser_shouldReturnException_whenUserRegistered(){
        RegisterRequest request = new RegisterRequest();
        String email = "test@mail.ru";
        request.setEmail(email);
        String namePass = "test";
        request.setUsername(namePass);
        request.setPassword(namePass);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(UserExistsException.class, () -> userService.createUser(request));

        verifyNoInteractions(redisTemplate);
        verifyNoInteractions(kafkaProducer);
    }
}
