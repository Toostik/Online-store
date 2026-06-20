package com.example.apigateway.service.whitelist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhitelistService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isWhitelisted(String key) {

        try {

            return redisTemplate.hasKey(key);

        }
        catch (Exception ex) {

            log.warn(
                    "Redis unavailable, whitelist disabled", ex);

            return false;
        }

    }

    public void whitelistUser(
            Long userId,
            Duration duration) {

        redisTemplate.opsForValue()
                .set(
                        "whitelist:user:" + userId,
                        true,
                        duration
                );

    }

    public void whitelistIp(
            String ip,
            Duration duration) {

        redisTemplate.opsForValue()
                .set(
                        "whitelist:ip:" + ip,
                        true,
                        duration
                );
    }

    public void removeUser(Long userId) {

        redisTemplate.delete(
                "whitelist:user:" + userId
        );

    }

    public void removeIp(String ip) {

        redisTemplate.delete(
                "whitelist:ip:" + ip
        );

    }

}
