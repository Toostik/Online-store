package com.example.authservice.dto.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
