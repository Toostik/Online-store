package com.example.notificationservice.dao.notification;

import com.example.notificationservice.entity.enums.NotificationStatus;
import com.example.notificationservice.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByStatus(NotificationStatus status);

}