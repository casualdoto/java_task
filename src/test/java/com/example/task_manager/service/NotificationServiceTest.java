package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.repository.impl.InMemoryNotificationRepository;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    private Notification testNotification;
    private UUID notificationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        notificationRepository = new InMemoryNotificationRepository();
        notificationService = new NotificationServiceImpl(notificationRepository);
        
        notificationId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testNotification = new Notification("Test Notification", userId);
        testNotification.setId(notificationId);
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Act
        Notification result = notificationService.createNotification(testNotification);

        // Assert
        assertNotNull(result);
        assertEquals(testNotification.getMessage(), result.getMessage());
        assertEquals(testNotification.getUserId(), result.getUserId());
        
        // Проверяем, что уведомление сохранено в репозитории
        Optional<Notification> savedNotification = notificationRepository.findById(result.getId());
        assertTrue(savedNotification.isPresent());
        assertEquals(testNotification.getMessage(), savedNotification.get().getMessage());
    }

    @Test
    void findAllByUserId_ShouldReturnUserNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        
        Notification anotherNotification = new Notification("Another Notification", userId);
        anotherNotification.setId(UUID.randomUUID());
        notificationRepository.save(anotherNotification);
        
        // Добавляем уведомление другого пользователя, которое не должно быть возвращено
        UUID anotherUserId = UUID.randomUUID();
        Notification otherUserNotification = new Notification("Other User Notification", anotherUserId);
        notificationRepository.save(otherUserNotification);

        // Act
        List<Notification> result = notificationService.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(n -> n.getMessage().equals(testNotification.getMessage())));
        assertTrue(result.stream().anyMatch(n -> n.getMessage().equals(anotherNotification.getMessage())));
        assertFalse(result.stream().anyMatch(n -> n.getMessage().equals(otherUserNotification.getMessage())));
    }

    @Test
    void findPendingByUserId_ShouldReturnPendingNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        
        // Создаем непрочитанное уведомление
        Notification pendingNotification = new Notification("Pending Notification", userId);
        pendingNotification.setId(UUID.randomUUID());
        notificationRepository.save(pendingNotification);
        
        // Создаем прочитанное уведомление, которое не должно считаться ожидающим
        Notification readNotification = new Notification("Read Notification", userId);
        readNotification.setId(UUID.randomUUID());
        readNotification.setRead(true);
        notificationRepository.save(readNotification);

        // Act
        List<Notification> result = notificationService.findPendingByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(n -> n.getMessage().equals(testNotification.getMessage())));
        assertTrue(result.stream().anyMatch(n -> n.getMessage().equals(pendingNotification.getMessage())));
        assertFalse(result.stream().anyMatch(n -> n.getMessage().equals(readNotification.getMessage())));
    }

    @Test
    void markAsRead_WhenNotificationExists_ShouldMarkAsRead() {
        // Arrange
        notificationRepository.save(testNotification);
        assertFalse(testNotification.isRead());

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        Optional<Notification> updatedNotification = notificationRepository.findById(notificationId);
        assertTrue(updatedNotification.isPresent());
        assertTrue(updatedNotification.get().isRead());
    }

    @Test
    void markAsRead_WhenNotificationDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        notificationService.markAsRead(nonExistentId);

        // Assert
        // Никаких ошибок быть не должно, сервис должен просто ничего не сделать
    }
} 