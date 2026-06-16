package com.example.paymentservice.feign;

import com.example.paymentservice.dto.order.request.OrderPaymentInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "order-service",
        url = "${services.order.url}"
)
public interface OrderClient {

    @GetMapping("/internal/orders/{id}/payment-info")
    OrderPaymentInfoResponse getPaymentInfo(
            @PathVariable Long id
    );

}
