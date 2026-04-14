package com.example.userservice.service;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.kafka.KafkaJsonProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaJsonProducer kafkaJsonProducer;

    public UserDto createUser(RegisterRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        kafkaJsonProducer.sendMessage(user.toDto());
        return user.toDto();
    }

    public UserDto getUser(Long userId) {
       User user = userRepository.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
       return user.toDto();
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        return user.toDto();
    }
}
