package com.example.task_manager.service;

import com.example.task_manager.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    Notification createNotification(Notification notification);
    List<Notification> findAllByUserId(UUID userId);
    List<Notification> findPendingByUserId(UUID userId);
    void markAsRead(UUID id);
} 