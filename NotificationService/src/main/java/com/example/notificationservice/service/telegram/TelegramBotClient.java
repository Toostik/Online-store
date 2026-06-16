package com.example.notificationservice.service.telegram;
import com.example.notificationservice.config.telegram.TelegramProperties;
import com.example.notificationservice.dao.notification.NotificationRepository;
import com.example.notificationservice.entity.enums.NotificationStatus;
import com.example.notificationservice.entity.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotClient {

    private final RestClient restClient;
    private final TelegramProperties telegramProperties;

    public void sendMessage(String text) {

        restClient.post()
                .uri(
                        "https://api.telegram.org/bot{token}/sendMessage",
                        telegramProperties.token()
                )
                .body(
                        Map.of(
                                "chat_id", telegramProperties.chatId(),
                                "text", text
                        )
                )
                .retrieve()
                .toBodilessEntity();

    }




}