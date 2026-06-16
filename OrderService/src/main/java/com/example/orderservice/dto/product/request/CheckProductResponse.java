package com.example.orderservice.dto.product.request;

import com.example.orderservice.dto.product.ProductAvailability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckProductResponse {
    Map<Long, ProductAvailability> productAvailability;
}
