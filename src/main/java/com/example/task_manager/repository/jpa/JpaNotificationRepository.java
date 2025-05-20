package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("prod")
public interface JpaNotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepository {
    List<Notification> findByUserIdAndReadFalse(UUID userId);
    List<Notification> findByUserId(UUID userId);
    
    @Override
    default List<Notification> findPendingByUserId(UUID userId) {
        return findByUserIdAndReadFalse(userId);
    }
} 