package com.example.task_manager.repository;

import com.example.task_manager.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends Repository<Notification> {
    List<Notification> findByUserId(UUID userId);
    List<Notification> findPendingByUserId(UUID userId);
} 