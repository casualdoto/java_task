package com.example.task_manager.service.impl;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    @Override
    public List<Notification> findAllByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    @Override
    public List<Notification> findPendingByUserId(UUID userId) {
        return notificationRepository.findPendingByUserId(userId);
    }
    
    @Override
    public void markAsRead(UUID id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    
    @Override
    @Async("taskExecutor")
    public void createNotificationsAsync(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            log.info("Асинхронное создание 0 уведомлений");
            log.info("Завершено асинхронное создание уведомлений");
            return;
        }
        
        log.info("Асинхронное создание {} уведомлений", notifications.size());
        
        for (Notification notification : notifications) {
            try {
                notificationRepository.save(notification);
                log.debug("Создано уведомление: {}", notification.getMessage());
            } catch (Exception e) {
                log.error("Ошибка при создании уведомления: {}", notification.getMessage(), e);
            }
        }
        
        log.info("Завершено асинхронное создание уведомлений");
    }
} 