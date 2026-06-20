package com.example.apigateway.dto.event;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RateLimitExceededEvent(

        UUID eventId,

        String userId,

        String ip,

        String endpoint,

        Integer limit,

        Instant timestamp

) {
}
