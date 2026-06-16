package com.example.paymentservice.service.integration;

import com.example.paymentservice.dto.order.request.OrderPaymentInfoResponse;
import com.example.paymentservice.feign.OrderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderClient orderClient;

    public OrderPaymentInfoResponse getOrderPaymentInfo(Long orderId){
        return orderClient.getPaymentInfo(
                orderId
        );
    }
}
