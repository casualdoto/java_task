package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("dev")
public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<UUID, Notification> notifications = new ConcurrentHashMap<>();

    @Override
    public Notification save(Notification entity) {
        notifications.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return Optional.ofNullable(notifications.get(id));
    }

    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    @Override
    public void delete(UUID id) {
        notifications.remove(id);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findPendingByUserId(UUID userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.isRead())
                .collect(Collectors.toList());
    }
} 