package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<UUID, Notification> notifications = new ConcurrentHashMap<>();

    @Override
    public <S extends Notification> S save(S entity) {
        notifications.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Notification> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(entity -> notifications.put(entity.getId(), entity));
        return entities;
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return Optional.ofNullable(notifications.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return notifications.containsKey(id);
    }

    @Override
    public Iterable<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    @Override
    public Iterable<Notification> findAllById(Iterable<UUID> ids) {
        List<Notification> result = new ArrayList<>();
        ids.forEach(id -> {
            if (notifications.containsKey(id)) {
                result.add(notifications.get(id));
            }
        });
        return result;
    }

    @Override
    public long count() {
        return notifications.size();
    }

    @Override
    public void deleteById(UUID id) {
        notifications.remove(id);
    }

    @Override
    public void delete(Notification entity) {
        notifications.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        ids.forEach(notifications::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends Notification> entities) {
        entities.forEach(entity -> notifications.remove(entity.getId()));
    }

    @Override
    public void deleteAll() {
        notifications.clear();
    }

    @Override
    public List<Notification> findByUserIdAndReadFalse(UUID userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.isRead())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findPendingByUserId(UUID userId) {
        return findByUserIdAndReadFalse(userId);
    }
} 