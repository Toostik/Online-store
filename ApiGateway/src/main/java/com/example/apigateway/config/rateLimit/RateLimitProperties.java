package com.example.apigateway.config.rateLimit;

import com.example.apigateway.dto.BucketConfig;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "rate-limit")
@Component
@Data
public class RateLimitProperties {

    private BucketConfig defaultConfig;

    private Map<String, BucketConfig> endpoints;

}
