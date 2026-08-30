package com.example.productservice.dto.error;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        Integer status,
        String error,
        String message,
        Instant timestamp
) {
}
