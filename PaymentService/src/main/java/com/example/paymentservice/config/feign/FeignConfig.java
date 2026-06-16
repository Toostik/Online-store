package com.example.paymentservice.config.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {

                String token = jwtAuth.getToken().getTokenValue();

                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
