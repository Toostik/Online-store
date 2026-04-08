package com.example.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient userServiceWebClient(){
        return WebClient.builder()
                .baseUrl("http://user-service:8082")
                .build();
    }
    @Bean
    public WebClient productServiceWebClient(){
        return WebClient.builder()
                .baseUrl("http://product-service:8083")
                .build();
    }
}
