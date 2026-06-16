package com.example.authservice.service.integration;

import com.example.authservice.dto.user.UserDto;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.exceptions.user.UserServiceException;
import com.example.authservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    public List<String> getRoles(Long id) {
        log.debug("Calling UserService: getRoles id={}", id);
        try {
            UserDto user = userClient.getUser(id);
            return List.of(user.getRole());
        }catch (Exception e){
            log.error("UserService call failed", e);
            throw new UserServiceException("User service exception");
        }
    }

    public UserDto createUser(RegisterRequest request) {
        log.info("Calling UserService: createUser email={}", request.getEmail());
        try {
            return userClient.createUser(request);
        }catch (Exception e){
            log.error("UserService call failed", e);
            throw new UserServiceException("User service exception");
        }
    }

    public UserDto getUserByEmail(String email) {
        log.debug("Calling UserService: getUserByEmail email={}", email);
        try {
            return userClient.getUserByEmail(email);
        }catch (Exception e){
            log.error("UserService call failed", e);
            throw new UserServiceException("User service exception");
        }
    }

    public UserDto authenticate(LoginRequest request){
        return userClient.authenticate(request);
    }

}
