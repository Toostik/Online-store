package com.example.authservice.service.integration;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.user.UserDto;
import com.example.authservice.exceptions.user.UserServiceException;
import com.example.authservice.feign.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    @Retry(name = "user-service")
    @CircuitBreaker(
            name = "user-service",
            fallbackMethod = "rolesFallback"
    )
    public List<String> getRoles(Long id) {
        log.debug("Calling UserService: getRoles id={}", id);
        UserDto user = userClient.getUser(id);
        return List.of(user.getRole());

    }

    private List<String> rolesFallback(
            Long id,
            Throwable ex
    ) {

        log.error("Cannot load roles", ex);

        throw new UserServiceException(
                "Cannot obtain user roles"
        );
    }


    @Retry(name = "user-service")
    @CircuitBreaker(
            name = "user-service",
            fallbackMethod = "createUserFallback"
    )
    public UserDto createUser(RegisterRequest request) {
        log.info("Calling UserService: createUser email={}", request.getEmail());
        return userClient.createUser(request);

    }
    private UserDto createUserFallback(
            RegisterRequest request,
            Throwable ex
    ) {

        log.error("UserService unavailable", ex);

        throw new UserServiceException(
                "User service is temporarily unavailable"
        );
    }

    @Retry(name = "user-service")
    @CircuitBreaker(
            name = "user-service",
            fallbackMethod = "authenticateFallback"
    )
    public UserDto authenticate(LoginRequest request) {
        return userClient.authenticate(request);
    }

    private UserDto authenticateFallback(
            LoginRequest request,
            Throwable ex
    ) {

        log.error("UserService unavailable", ex);

        throw new UserServiceException(
                "Authentication service unavailable"
        );
    }



}
