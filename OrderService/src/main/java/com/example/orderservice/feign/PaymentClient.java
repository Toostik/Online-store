package com.example.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service", url = "${services.payment.url}")
public interface PaymentClient {

}