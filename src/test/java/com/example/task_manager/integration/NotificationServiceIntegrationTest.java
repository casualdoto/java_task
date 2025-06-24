package com.example.task_manager.integration;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.User;
import com.example.task_manager.repository.impl.InMemoryNotificationRepository;
import com.example.task_manager.repository.impl.InMemoryUserRepository;
import com.example.task_manager.service.NotificationService;
import com.example.task_manager.service.UserService;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import com.example.task_manager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceIntegrationTest {

    private UserService userService;
    private NotificationService notificationService;
    
    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Создаем репозитории
        var userRepository = new InMemoryUserRepository();
        var notificationRepository = new InMemoryNotificationRepository();

        // Создаем сервисы
        userService = new UserServiceImpl(userRepository);
        notificationService = new NotificationServiceImpl(notificationRepository);

        // Создаем тестовых пользователей
        testUser = new User("testUser", "password123");
        testUser = userService.register(testUser);
        
        anotherUser = new User("anotherUser", "password456");
        anotherUser = userService.register(anotherUser);
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Arrange
        Notification notification = new Notification("Test Notification", testUser.getId());

        // Act
        Notification savedNotification = notificationService.createNotification(notification);

        // Assert
        assertNotNull(savedNotification);
        assertEquals(notification.getMessage(), savedNotification.getMessage());
        assertEquals(notification.getUserId(), savedNotification.getUserId());

        // Проверяем, что уведомление сохранено в репозитории
        List<Notification> userNotifications = notificationService.findAllByUserId(testUser.getId());
        assertEquals(1, userNotifications.size());
        assertEquals(savedNotification.getId(), userNotifications.get(0).getId());
    }

    @Test
    void findAllByUserId_ShouldReturnOnlyUserNotifications() {
        // Arrange
        notificationService.createNotification(new Notification("Test Notification 1", testUser.getId()));
        notificationService.createNotification(new Notification("Test Notification 2", testUser.getId()));
        notificationService.createNotification(new Notification("Another User Notification", anotherUser.getId()));

        // Act
        List<Notification> userNotifications = notificationService.findAllByUserId(testUser.getId());

        // Assert
        assertEquals(2, userNotifications.size());
        userNotifications.forEach(notification -> assertEquals(testUser.getId(), notification.getUserId()));
    }

    @Test
    void findPendingByUserId_ShouldReturnOnlyUnreadNotifications() {
        // Arrange
        Notification notification1 = notificationService.createNotification(new Notification("Test Notification 1", testUser.getId()));
        Notification notification2 = notificationService.createNotification(new Notification("Test Notification 2", testUser.getId()));
        
        // Отмечаем одно уведомление как прочитанное
        notificationService.markAsRead(notification1.getId());

        // Act
        List<Notification> pendingNotifications = notificationService.findPendingByUserId(testUser.getId());

        // Assert
        assertEquals(1, pendingNotifications.size());
        assertEquals(notification2.getId(), pendingNotifications.get(0).getId());
        assertFalse(pendingNotifications.get(0).isRead());
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        Notification notification = notificationService.createNotification(new Notification("Test Notification", testUser.getId()));
        assertFalse(notification.isRead());

        // Act
        notificationService.markAsRead(notification.getId());

        // Assert
        List<Notification> allNotifications = notificationService.findAllByUserId(testUser.getId());
        assertEquals(1, allNotifications.size());
        assertTrue(allNotifications.get(0).isRead());
    }

    @Test
    void markAsRead_WhenNotificationDoesNotExist_ShouldDoNothing() {
        // Arrange
        Notification notification = notificationService.createNotification(new Notification("Test Notification", testUser.getId()));
        
        // Act - пытаемся отметить несуществующее уведомление
        notificationService.markAsRead(java.util.UUID.randomUUID());

        // Assert - проверяем, что существующее уведомление не изменилось
        List<Notification> allNotifications = notificationService.findAllByUserId(testUser.getId());
        assertEquals(1, allNotifications.size());
        assertFalse(allNotifications.get(0).isRead());
    }
} 