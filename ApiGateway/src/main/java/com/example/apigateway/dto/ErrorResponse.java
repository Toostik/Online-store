package com.example.apigateway.dto;

import lombok.Builder;

@Builder
public record ErrorResponse (
        Integer status,
        String error,
        String message,
        Integer limit
) {
}
