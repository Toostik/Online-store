package com.example.userservice.service.user;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.user.UserDto;
import com.example.userservice.dto.request.LoginRequest;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.entity.enums.Role;
import com.example.userservice.entity.user.User;
import com.example.userservice.exceptions.PasswordWrongException;
import com.example.userservice.exceptions.UserExistsException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.service.security.SecurityService;
import com.example.userservice.service.file.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer kafkaProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SecurityService securityService;
    private final MinioService minioService;

    public UserDto createUser(RegisterRequest request) {
        log.info("USER_CREATE_START email={}", request.getEmail());
        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("USER_ALREADY_EXISTS email={}", request.getEmail());
            throw new UserExistsException("User already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);

        User saved = userRepository.save(user);

        log.info("USER_CREATED id={}", saved.getId());

        String key = "user:" + saved.getId();
        redisTemplate.opsForValue().set(key, saved.toDto(), Duration.ofDays(1));

        log.info("KAFKA_SEND users-registered userId={}", saved.getId());
        kafkaProducer.sendMessage("users-registered",saved.toDto());


        return saved.toDto();
    }

    public UserDto authenticate(LoginRequest request) {
        User user = userRepository.getUserByEmail(request.getEmail()).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordWrongException("Password is incorrect");
        }

        return user.toDto();
    }

    public void uploadImages(MultipartFile file) {
        Long userId = securityService.getCurrentUserId();
        log.info("Upload file for user -> {}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
        String filename = minioService.upload(file);
        user.setAvatarImagePath(filename);
        userRepository.save(user);
        log.info("Uploaded avatar for user -> {}", userId);
    }

    public void addBalance(BigDecimal balance) {
        Long userId = securityService.getCurrentUserId();
        User user = userRepository.getUserById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
        user.setBalance(user.getBalance().add(balance));
        User saved =  userRepository.save(user);
        String key = "user:" + userId;
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key,saved.toDto(),Duration.ofDays(1));
    }
}
