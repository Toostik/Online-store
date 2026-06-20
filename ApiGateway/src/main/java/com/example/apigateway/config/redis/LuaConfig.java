package com.example.apigateway.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class LuaConfig {

    @Bean
    public RedisScript<Long> tokenBucketScript(){

        Resource resource =
                new ClassPathResource(
                        "scripts/token_bucket.lua"
                );

        return RedisScript.of(
                resource,
                Long.class
        );

    }
}
