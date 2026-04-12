package com.example.productservice.config;

import com.example.productservice.jwt.JwtHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/products/prices").permitAll()
                        .requestMatchers("/api/products/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtHeaderFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
