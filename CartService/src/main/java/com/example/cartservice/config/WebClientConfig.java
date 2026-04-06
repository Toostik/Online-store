package com.example.cartservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder){
        return builder
                .baseUrl("http://localhost:8082")
                .build();
    }

    @Bean
    public WebClient productServiceWebClient(WebClient.Builder builder){
        return builder
                .baseUrl("http://localhost:8083")
                .build();
    }

}
