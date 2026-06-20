package com.example.apigateway.dto;

import lombok.Builder;

@Builder
public record RateLimitContext(

        String userId,

        String ip,

        String endpoint

) {
}
