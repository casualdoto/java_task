package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;
    private UUID notificationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testNotification = new Notification("Test Notification", userId);
        testNotification.setId(notificationId);
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        Notification result = notificationService.createNotification(testNotification);

        // Assert
        assertNotNull(result);
        assertEquals(testNotification.getMessage(), result.getMessage());
        assertEquals(testNotification.getUserId(), result.getUserId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void findAllByUserId_ShouldReturnUserNotifications() {
        // Arrange
        Notification anotherNotification = new Notification("Another Notification", userId);
        anotherNotification.setId(UUID.randomUUID());
        List<Notification> notifications = Arrays.asList(testNotification, anotherNotification);
        when(notificationRepository.findByUserId(userId)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationService.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByUserId(userId);
    }

    @Test
    void findPendingByUserId_ShouldReturnPendingNotifications() {
        // Arrange
        Notification pendingNotification = new Notification("Pending Notification", userId);
        pendingNotification.setId(UUID.randomUUID());
        List<Notification> pendingNotifications = Arrays.asList(testNotification, pendingNotification);
        when(notificationRepository.findPendingByUserId(userId)).thenReturn(pendingNotifications);

        // Act
        List<Notification> result = notificationService.findPendingByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findPendingByUserId(userId);
    }

    @Test
    void markAsRead_WhenNotificationExists_ShouldMarkAsRead() {
        // Arrange
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, times(1)).save(any(Notification.class));
        assertTrue(testNotification.isRead());
    }

    @Test
    void markAsRead_WhenNotificationDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(notificationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        notificationService.markAsRead(nonExistentId);

        // Assert
        verify(notificationRepository, times(1)).findById(nonExistentId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
} 