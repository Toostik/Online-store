package com.example.productservice.kafka;


import com.example.productservice.dto.OrderDto;
import com.example.productservice.dto.OrderItemDto;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaJsonConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonConsumer.class);
    private final ProductService productService;
    @KafkaListener(topics = "orders-created", groupId = "products-consumers-group")
    public void consume(OrderDto orderDto, Acknowledgment ack) {

        LOGGER.info("Order received");

        Map<Long, Integer> products = orderDto.getItems().stream().collect(Collectors.toMap(
                OrderItemDto::getProduct_id,
                OrderItemDto::getQuantity
        ));

        productService.decreaseQuantity(products);

        ack.acknowledge();


    }
}