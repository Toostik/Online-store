package com.example.notificationservice.service.notification;


import com.example.notificationservice.service.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TelegramService telegramService;


    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        telegramService.handleOrderConfirmed(event);
    }

    public void handleOrderCancelled(OrderCancelledEvent event){
        telegramService.handleOrderCancelled(event);
    }

}