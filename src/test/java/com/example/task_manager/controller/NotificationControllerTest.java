package com.example.task_manager.controller;

import com.example.task_manager.model.Notification;
import com.example.task_manager.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

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
    void getAllUserNotifications_ShouldReturnUserNotifications() throws Exception {
        // Arrange
        Notification anotherNotification = new Notification("Another Notification", userId);
        anotherNotification.setId(UUID.randomUUID());
        List<Notification> notifications = Arrays.asList(testNotification, anotherNotification);
        when(notificationService.findAllByUserId(userId)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].message").value(testNotification.getMessage()))
                .andExpect(jsonPath("$[1].message").value(anotherNotification.getMessage()));
    }

    @Test
    void getPendingUserNotifications_ShouldReturnPendingNotifications() throws Exception {
        // Arrange
        Notification pendingNotification = new Notification("Pending Notification", userId);
        pendingNotification.setId(UUID.randomUUID());
        List<Notification> pendingNotifications = Arrays.asList(testNotification, pendingNotification);
        when(notificationService.findPendingByUserId(userId)).thenReturn(pendingNotifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/pending")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].message").value(testNotification.getMessage()))
                .andExpect(jsonPath("$[1].message").value(pendingNotification.getMessage()));
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsReadAndReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(notificationService).markAsRead(notificationId);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/" + notificationId + "/read"))
                .andExpect(status().isNoContent());
        
        verify(notificationService, times(1)).markAsRead(notificationId);
    }
} 