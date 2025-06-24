package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.TaskOverdueEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaMessageListenerTest {

    @Mock
    private NotificationService notificationService;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private KafkaMessageListener kafkaMessageListener;

    @BeforeEach
    void setUp() {
        kafkaMessageListener = new KafkaMessageListener(notificationService, objectMapper);
    }

    @Test
    void handleTaskOverdueEvent_ShouldCreateNotification() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskOverdueEvent event = new TaskOverdueEvent(
                taskId,
                "Test Task",
                userId,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        
        when(objectMapper.convertValue(any(), eq(TaskOverdueEvent.class))).thenReturn(event);

        // Act
        kafkaMessageListener.handleTaskOverdueEvent(event);

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).createNotification(captor.capture());
        
        Notification notification = captor.getValue();
        assertEquals(userId, notification.getUserId());
        assertTrue(notification.getMessage().contains("Test Task"));
        assertTrue(notification.getMessage().contains("просрочена"));
        assertFalse(notification.isRead());
    }

    @Test
    void handleTaskOverdueEvent_WithNullEvent_ShouldNotThrowException() {
        // Arrange
        when(objectMapper.convertValue(any(), eq(TaskOverdueEvent.class)))
            .thenThrow(new RuntimeException("Conversion error"));

        // Act & Assert
        assertDoesNotThrow(() -> kafkaMessageListener.handleTaskOverdueEvent(null));
        verifyNoInteractions(notificationService);
    }

    @Test
    void handleTaskOverdueEvent_WhenServiceFails_ShouldNotThrowException() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskOverdueEvent event = new TaskOverdueEvent(
                taskId,
                "Test Task",
                userId,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        
        when(objectMapper.convertValue(any(), eq(TaskOverdueEvent.class))).thenReturn(event);
        doThrow(new RuntimeException("Service error"))
            .when(notificationService).createNotification(any());

        // Act & Assert
        assertDoesNotThrow(() -> kafkaMessageListener.handleTaskOverdueEvent(event));
        verify(notificationService).createNotification(any());
    }

    @Test
    void handleTaskOverdueEvent_ShouldCreateCorrectNotificationMessage() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime targetDate = LocalDateTime.of(2025, 5, 25, 10, 0);
        TaskOverdueEvent event = new TaskOverdueEvent(
                taskId,
                "Important Task",
                userId,
                targetDate,
                LocalDateTime.now()
        );
        
        when(objectMapper.convertValue(any(), eq(TaskOverdueEvent.class))).thenReturn(event);

        // Act
        kafkaMessageListener.handleTaskOverdueEvent(event);

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).createNotification(captor.capture());
        
        Notification notification = captor.getValue();
        assertTrue(notification.getMessage().contains("Important Task"));
        assertTrue(notification.getMessage().contains("просрочена"));
    }

    @Test
    void handleTaskOverdueEvent_WithSpecialCharactersInTitle_ShouldHandleCorrectly() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskOverdueEvent event = new TaskOverdueEvent(
                taskId,
                "Task with 'quotes' & symbols",
                userId,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        
        when(objectMapper.convertValue(any(), eq(TaskOverdueEvent.class))).thenReturn(event);

        // Act & Assert
        assertDoesNotThrow(() -> kafkaMessageListener.handleTaskOverdueEvent(event));
        
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).createNotification(captor.capture());
        
        Notification notification = captor.getValue();
        assertTrue(notification.getMessage().contains("Task with 'quotes' & symbols"));
    }
} 