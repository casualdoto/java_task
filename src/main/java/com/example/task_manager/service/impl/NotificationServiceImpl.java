package com.example.task_manager.service.impl;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
} 