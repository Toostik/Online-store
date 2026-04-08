package com.example.productservice.kafka;


import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaJsonConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonConsumer.class);
    private final ProductService productService;
    @KafkaListener(topics = "orders-created", groupId = "products-consumers-group")
    public void consume(Map<String, Integer> quantityOfProducts, Acknowledgment ack) {
        LOGGER.info("Order received");

        Map<Long, Integer> products = quantityOfProducts.entrySet().stream()
                .collect(Collectors.toMap(e -> Long.parseLong(e.getKey()), Map.Entry::getValue));

        productService.decreaseQuantity(products);

        ack.acknowledge();


    }
}