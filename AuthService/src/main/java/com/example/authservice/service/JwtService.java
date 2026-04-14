package com.example.authservice.service;

import com.example.authservice.dto.request.AuthResponse;
import com.example.authservice.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;

    private static final String REFRESH_PREFIX = "refresh:";

    public AuthResponse generateTokens(String userId, List<String> roles) {

        String accessToken = jwtUtil.createAccessToken(userId, roles);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + userId,
                refreshToken,
                Duration.ofDays(7)
        );

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {

        jwtUtil.validateRefreshToken(refreshToken);

        Claims claims = jwtUtil.parseToken(refreshToken);
        String userId = claims.getSubject();

        Object storedObj = redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);

        if (storedObj == null) {
            throw new RuntimeException("Refresh token not found (user logged out?)");
        }

        String stored = storedObj.toString();

        if (!refreshToken.equals(stored)) {
            throw new RuntimeException("Invalid refresh token");
        }



        List<String> roles = userService.getRoles(Long.valueOf(userId));

        return new AuthResponse(jwtUtil.createAccessToken(userId, roles), refreshToken);
    }

    public void logout(String refreshToken) {

        jwtUtil.validateRefreshToken(refreshToken);

        Claims claims = jwtUtil.parseToken(refreshToken);
        String userId = claims.getSubject();

        if (redisTemplate.hasKey(REFRESH_PREFIX + userId)) {
            redisTemplate.delete(REFRESH_PREFIX + userId);
        }
        
    }

    public String extractUserId(String token) {
        return jwtUtil.parseToken(token).getSubject();
    }

    public String rotateRefreshToken(String userId) {

        String newRefresh = jwtUtil.createRefreshToken(userId);

        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                newRefresh,
                Duration.ofDays(7)
        );

        return newRefresh;
    }
}