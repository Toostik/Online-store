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
import com.example.userservice.service.user.builder.UserBuilder;
import com.example.userservice.service.user.cache.UserCacheService;
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
    private final SecurityService securityService;
    private final MinioService minioService;
    private final UserBuilder userBuilder;
    private final UserCacheService userCacheService;

    public UserDto createUser(RegisterRequest request) {

        log.info("USER_CREATE_START email={}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("USER_ALREADY_EXISTS email={}", request.getEmail());
            throw new UserExistsException("User already registered");
        }

        User user = userBuilder.create(request);

        User saved = userRepository.save(user);

        log.info("USER_CREATED id={}", saved.getId());

        userCacheService.save(saved);

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

        User user = userRepository.getUserById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        String filename = minioService.upload(file);

        user.setAvatarImagePath(filename);

        userCacheService.remove(userId);

        log.info("Uploaded avatar for user -> {}", userId);
    }

    public void addBalance(BigDecimal balance) {

        Long userId = securityService.getCurrentUserId();

        User user = userRepository.getUserById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        user.setBalance(user.getBalance().add(balance));

        userCacheService.remove(userId);

    }
}
