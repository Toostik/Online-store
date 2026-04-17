package com.example.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    @NotNull(message = "The cart's ID must be filled in")
    private Long id;
    @NotNull(message = "The user's ID must be filled in")
    private Long userId;
    @NotEmpty(message = "There must be items in the basket")
    private List<CartItemDto> items;
}
