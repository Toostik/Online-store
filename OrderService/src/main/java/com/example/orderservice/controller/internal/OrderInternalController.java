package com.example.orderservice.controller.internal;

import com.example.orderservice.dto.order.request.OrderPaymentInfoResponse;
import com.example.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/orders")
public class OrderInternalController {

    private final OrderService orderService;

    @GetMapping("/{id}/payment-info")
    public OrderPaymentInfoResponse getPaymentInfo(
            @PathVariable Long id
    ){

        return orderService.getPaymentInfo(
                id
        );

    }

}
