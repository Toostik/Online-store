package com.example.productservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class LuaConfig {

    @Bean
    public RedisScript<Long> reserveFlashSaleScript() {

        return RedisScript.of(
                new ClassPathResource("scripts/reserve_flash_sale.lua"),
                Long.class
        );
    }

}
