package com.example.authservice.jwt.cache;

import com.example.authservice.dto.user.UserDto;
import com.example.authservice.exceptions.token.TokenException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_PREFIX = "refresh:";

    private final Cache<String, String> cache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofDays(7))
                    .build();

    public void save(Long userId, String refreshToken){

        String key = REFRESH_PREFIX + userId;

        cache.put(
                key,
                refreshToken
        );

        try {

            redisTemplate.opsForValue().set(
                    key,
                    refreshToken,
                    Duration.ofDays(7)
            );

        } catch (Exception e){
            log.warn("Redis connect exception", e);

        }

    }

    public String get(String userId){

        String key = REFRESH_PREFIX + userId;

        String token =
                cache.getIfPresent(key);

        if(token != null){
            return token;
        }

        try {

            Object redisToken =
                    redisTemplate.opsForValue().get(key);

            if(redisToken != null){

                cache.put(key, redisToken.toString());

                return redisToken.toString();
            }

        } catch (Exception e){

            log.warn("Redis connect exception", e);

        }

        throw new TokenException("Refresh token not found in cache (user logged out?)");

    }

    public void remove(String userId){

        String key = REFRESH_PREFIX + userId;

        if(cache.getIfPresent(key) != null){
            cache.invalidate(key);
        }

        try{

            redisTemplate.delete(key);

        } catch (Exception e){

            log.warn("Redis connect exception", e);

        }

    }



}
