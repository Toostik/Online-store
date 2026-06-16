package com.example.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service", url = "http://payment-service:8086")
public interface PaymentClient {

}