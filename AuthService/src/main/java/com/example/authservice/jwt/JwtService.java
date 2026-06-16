package com.example.authservice.jwt;

import com.example.authservice.dto.user.UserDto;
import com.example.authservice.dto.response.AuthResponse;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.exceptions.token.TokenException;
import com.example.authservice.exceptions.user.UserIdOrRoleException;
import com.example.authservice.exceptions.user.UserServiceException;
import com.example.authservice.service.integration.UserService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private static final String REFRESH_PREFIX = "refresh:";

    public AuthResponse registerAndGenerateTokens(RegisterRequest request) {
        log.info("AUTH_REGISTER_START email={}", request.getEmail());

        UserDto user = userService.createUser(request);

        if (user == null) {
            throw new UserServiceException("User is null");
        }

        if (user.getId() == null || user.getRole().isBlank()) {
            throw new UserIdOrRoleException("Role or id is null");
        }

        log.info("USER_CREATED id={}", user.getId());

        String accessToken = jwtUtil.createAccessToken(String.valueOf(user.getId()), List.of(user.getRole()));
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));

        log.info("TOKENS_CREATED userId={}", user.getId());

        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + user.getId(),
                refreshToken,
                Duration.ofDays(7)
        );


        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse loginAndGenerateTokens(LoginRequest request) {
        log.info("AUTH_LOGIN_START email={}", request.getEmail());

        UserDto user = userService.authenticate(request);

        if (user.getId() == null || user.getRole() == null || user.getRole().isBlank()) {
            throw new UserIdOrRoleException("Role or id is null");
        }

        log.debug("USER_FOUND id={}", user.getId());

        String accessToken = jwtUtil.createAccessToken(String.valueOf(user.getId()), List.of(user.getRole()));
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));

        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + user.getId(),
                refreshToken,
                Duration.ofDays(7)
        );

        log.info("AUTH_SUCCESS userId={}", user.getId());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        log.info("TOKEN_REFRESH_START");
        jwtUtil.validateRefreshToken(refreshToken);

        Claims claims = jwtUtil.parseToken(refreshToken);
        String userId = claims.getSubject();

        log.debug("TOKEN_PARSED userId={}", userId);

        Object storedObj = redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);

        if (storedObj == null) {
            throw new TokenException("Refresh token not found in cache (user logged out?)");
        }

        String stored = storedObj.toString();

        if (!refreshToken.equals(stored)) {
            throw new TokenException("Invalid refresh token");
        }

        List<String> roles = userService.getRoles(Long.valueOf(userId));

        if (roles == null || roles.isEmpty()) {
            throw new UserIdOrRoleException("Role or id is null");
        }
        log.info("TOKEN_REFRESH_SUCCESS userId={}", userId);
        return new AuthResponse(jwtUtil.createAccessToken(userId, roles), refreshToken);
    }

    public void logout(String refreshToken) {
        log.info("LOGOUT_START");
        jwtUtil.validateRefreshToken(refreshToken);

        Claims claims = jwtUtil.parseToken(refreshToken);
        String userId = claims.getSubject();

        if (redisTemplate.hasKey(REFRESH_PREFIX + userId)) {
            redisTemplate.delete(REFRESH_PREFIX + userId);
        }

        log.info("User logged out by id -> {}", userId);

    }

    public String extractUserId(String token) {
        return jwtUtil.parseToken(token).getSubject();
    }

    public String rotateRefreshToken(String userId) {

        log.info("ROTATE_REFRESH_TOKEN userId={}", userId);

        String newRefresh = jwtUtil.createRefreshToken(userId);

        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                newRefresh,
                Duration.ofDays(7)
        );

        return newRefresh;
    }


}