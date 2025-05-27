package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationRepository);
    }

    @Test
    void createNotificationsAsync_WithValidNotifications_ShouldSaveAll() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Notification notification1 = new Notification("Message 1", userId);
        Notification notification2 = new Notification("Message 2", userId);
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        
        when(notificationRepository.save(any(Notification.class)))
            .thenReturn(notification1)
            .thenReturn(notification2);

        // Act
        notificationService.createNotificationsAsync(notifications);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void createNotificationsAsync_WithEmptyList_ShouldNotCallRepository() {
        // Act
        notificationService.createNotificationsAsync(Collections.emptyList());

        // Assert
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void createNotificationsAsync_WhenRepositoryFails_ShouldContinueProcessing() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Notification notification1 = new Notification("Message 1", userId);
        Notification notification2 = new Notification("Message 2", userId);
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        
        // Первый вызов бросает исключение, второй проходит нормально
        when(notificationRepository.save(any(Notification.class)))
            .thenThrow(new RuntimeException("Database error"))
            .thenReturn(notification2);

        // Act & Assert
        assertDoesNotThrow(() -> notificationService.createNotificationsAsync(notifications));
        
        // Проверяем, что оба уведомления были обработаны
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void createNotificationsAsync_WithNullList_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> notificationService.createNotificationsAsync(null));
        
        // Assert
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void createNotificationsAsync_WithLargeList_ShouldProcessAll() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<Notification> notifications = Arrays.asList(
            new Notification("Message 1", userId),
            new Notification("Message 2", userId),
            new Notification("Message 3", userId),
            new Notification("Message 4", userId),
            new Notification("Message 5", userId)
        );
        
        when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificationService.createNotificationsAsync(notifications);

        // Assert
        verify(notificationRepository, times(5)).save(any(Notification.class));
    }
} 