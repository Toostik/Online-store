package com.example.notificationservice.service.telegram;

import com.example.notificationservice.dao.event.ProcessedEventRepository;
import com.example.notificationservice.dao.notification.NotificationRepository;

import com.example.notificationservice.entity.enums.NotificationChannel;
import com.example.notificationservice.entity.enums.NotificationStatus;
import com.example.notificationservice.entity.enums.NotificationType;
import com.example.notificationservice.entity.event.ProcessedEvent;
import com.example.notificationservice.entity.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final TelegramBotClient telegramBotClient;
    private final ProcessedEventRepository processedEventRepository;
    private final NotificationRepository notificationRepository;

    private boolean markProcessed(String eventId) {

        try {

            processedEventRepository.save(
                    new ProcessedEvent(eventId)
            );

            return true;
        }
        catch (DataIntegrityViolationException e) {

            return false;
        }
    }

    public void send(Notification notification) {

        try {

            telegramBotClient.sendMessage(
                    notification.getMessage()
            );

            notification.setStatus(
                    NotificationStatus.SENT
            );

            notification.setSentAt(
                    LocalDateTime.now()
            );

            notification.setErrorMessage(null);

            notificationRepository.save(notification);

            log.info(
                    "Notification sent. id={}",
                    notification.getId()
            );

        }
        catch (Exception e) {

            notification.setStatus(
                    NotificationStatus.FAILED
            );

            notification.setErrorMessage(
                    e.getMessage()
            );

            notificationRepository.save(notification);

            throw e;
        }

    }

    public void handleOrderConfirmed(OrderConfirmedEvent event) {



        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_ORDER_CONFIRMED_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        String message = """
                ✅ Order confirmed

                Order #%d

                Amount: %s ₸
                """
                .formatted(
                        event.orderId(),
                        event.amount()
                );

        Notification notification =
                Notification.builder()
                        .eventId(event.eventId())
                        .orderId(event.orderId())
                        .userId(event.userId())
                        .type(NotificationType.ORDER_CONFIRMED)
                        .channel(NotificationChannel.TELEGRAM)
                        .status(NotificationStatus.PENDING)
                        .message(message)
                        .createdAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);

        send(notification);
    }

    public void handleOrderCancelled(OrderCancelledEvent event) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_ORDER_CANCELLED_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        String message = """
            ❌ Order cancelled

            Order #%d

            Reason: %s
            """
                .formatted(
                        event.orderId(),
                        event.reason()
                );

        Notification notification =
                Notification.builder()
                        .eventId(event.eventId())
                        .orderId(event.orderId())
                        .userId(event.userId())
                        .type(NotificationType.ORDER_CANCELLED)
                        .channel(NotificationChannel.TELEGRAM)
                        .status(NotificationStatus.PENDING)
                        .message(message)
                        .createdAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);

        try {

            telegramBotClient.sendMessage(notification.getMessage());

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setErrorMessage(null);

            notificationRepository.save(notification);

        } catch (Exception e) {

            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());

            notificationRepository.save(notification);

            throw e;
        }

    }



}
