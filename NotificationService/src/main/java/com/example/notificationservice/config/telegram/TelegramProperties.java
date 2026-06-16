package com.example.notificationservice.config.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record TelegramProperties(
        String token,
        String chatId
) {
}
