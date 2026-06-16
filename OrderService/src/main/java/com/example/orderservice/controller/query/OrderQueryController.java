package com.example.orderservice.controller.query;

import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.dto.order.ProfileOrders;
import com.example.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderQueryController {

    private final OrderService orderService;

    @GetMapping("/my")
    public ResponseEntity<List<OrderDto>> getAllOrders(){
        log.info("ORDER_GET_ALL_CURRENT");
        return ResponseEntity.ok(orderService.getCurrentUserOrders());
    }

    @PostMapping("/my/items")
    public ResponseEntity<ProfileOrders> getRecentOrders(@RequestParam(required = false) Integer size){
        log.info("GET_RECENT_ITEMS");
        return ResponseEntity.ok(orderService.getRecentItems(size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id){
        log.info("ORDER_GET_BY_ID id={}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

}
