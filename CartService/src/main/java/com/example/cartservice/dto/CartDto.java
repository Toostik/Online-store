package com.example.cartservice.dto;

import com.example.cartservice.entity.CartItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItem> items;
}
