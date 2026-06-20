package com.example.apigateway.service.bucket;

import com.example.apigateway.config.rateLimit.RateLimitProperties;
import com.example.apigateway.dto.BucketConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
@RequiredArgsConstructor
public class BucketConfigResolver {

    private final RateLimitProperties properties;

    public BucketConfig resolve(ServerWebExchange exchange) {

        String path = exchange.getRequest()
                .getPath()
                .value();

        for (var entry : properties.getEndpoints().entrySet()) {

            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return properties.getDefaultConfig();
    }
}
