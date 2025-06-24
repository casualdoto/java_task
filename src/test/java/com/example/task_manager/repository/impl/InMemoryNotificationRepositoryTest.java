package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryNotificationRepositoryTest {

    private InMemoryNotificationRepository notificationRepository;
    private Notification testNotification;
    private UUID notificationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        notificationRepository = new InMemoryNotificationRepository();
        userId = UUID.randomUUID();
        testNotification = new Notification("Test Notification", userId);
        notificationId = testNotification.getId();
    }

    @Test
    void save_ShouldSaveAndReturnNotification() {
        // Act
        Notification savedNotification = notificationRepository.save(testNotification);

        // Assert
        assertNotNull(savedNotification);
        assertEquals(testNotification.getMessage(), savedNotification.getMessage());
        assertEquals(testNotification.getUserId(), savedNotification.getUserId());
    }

    @Test
    void findById_WhenNotificationExists_ShouldReturnNotification() {
        // Arrange
        notificationRepository.save(testNotification);

        // Act
        Optional<Notification> foundNotification = notificationRepository.findById(notificationId);

        // Assert
        assertTrue(foundNotification.isPresent());
        assertEquals(testNotification.getMessage(), foundNotification.get().getMessage());
    }

    @Test
    void findById_WhenNotificationDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Notification> foundNotification = notificationRepository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundNotification.isPresent());
    }

    @Test
    void findAll_WhenNoNotifications_ShouldReturnEmptyList() {
        // Act
        Iterable<Notification> notificationsIterable = notificationRepository.findAll();
        List<Notification> notifications = convertToList(notificationsIterable);

        // Assert
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void findAll_WhenNotificationsExist_ShouldReturnAllNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        Notification anotherNotification = new Notification("Another Notification", userId);
        notificationRepository.save(anotherNotification);

        // Act
        Iterable<Notification> notificationsIterable = notificationRepository.findAll();
        List<Notification> notifications = convertToList(notificationsIterable);

        // Assert
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
    }

    @Test
    void delete_WhenNotificationExists_ShouldDeleteNotification() {
        // Arrange
        notificationRepository.save(testNotification);

        // Act
        notificationRepository.deleteById(notificationId);
        Optional<Notification> deletedNotification = notificationRepository.findById(notificationId);

        // Assert
        assertFalse(deletedNotification.isPresent());
    }

    @Test
    void delete_WhenNotificationDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> notificationRepository.deleteById(nonExistentId));
    }

    @Test
    void findByUserId_ShouldReturnUserNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        Notification anotherNotification = new Notification("Another Notification", userId);
        notificationRepository.save(anotherNotification);
        
        // Create a notification for another user
        UUID anotherUserId = UUID.randomUUID();
        Notification anotherUserNotification = new Notification("Another User Notification", anotherUserId);
        notificationRepository.save(anotherUserNotification);

        // Act
        List<Notification> userNotifications = notificationRepository.findByUserId(userId);

        // Assert
        assertNotNull(userNotifications);
        assertEquals(2, userNotifications.size());
        for (Notification notification : userNotifications) {
            assertEquals(userId, notification.getUserId());
        }
    }

    @Test
    void findPendingByUserId_ShouldReturnUnreadNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        
        // Create a read notification
        Notification readNotification = new Notification("Read Notification", userId);
        readNotification.setRead(true);
        notificationRepository.save(readNotification);

        // Act
        List<Notification> pendingNotifications = notificationRepository.findPendingByUserId(userId);

        // Assert
        assertNotNull(pendingNotifications);
        assertEquals(1, pendingNotifications.size());
        assertEquals(testNotification.getId(), pendingNotifications.get(0).getId());
        assertFalse(pendingNotifications.get(0).isRead());
    }
    
    private <T> List<T> convertToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
    
    @Test
    void findByUserIdAndReadFalse_ShouldReturnUnreadUserNotifications() {
        // Arrange
        notificationRepository.save(testNotification); // не прочитано по умолчанию
        
        // Создаем прочитанное уведомление
        Notification readNotification = new Notification("Read Notification", userId);
        readNotification.setRead(true);
        notificationRepository.save(readNotification);
        
        // Создаем уведомление для другого пользователя
        UUID anotherUserId = UUID.randomUUID();
        Notification anotherUserNotification = new Notification("Another User Notification", anotherUserId);
        notificationRepository.save(anotherUserNotification);
        
        // Act
        List<Notification> unreadUserNotifications = notificationRepository.findByUserIdAndReadFalse(userId);
        
        // Assert
        assertEquals(1, unreadUserNotifications.size());
        assertEquals(testNotification.getId(), unreadUserNotifications.get(0).getId());
        assertFalse(unreadUserNotifications.get(0).isRead());
    }
    
    @Test
    void deleteAllById_ShouldRemoveSpecifiedNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        Notification anotherNotification = new Notification("Another Notification", userId);
        notificationRepository.save(anotherNotification);
        Notification thirdNotification = new Notification("Third Notification", userId);
        notificationRepository.save(thirdNotification);
        
        List<UUID> idsToDelete = Arrays.asList(testNotification.getId(), anotherNotification.getId());
        
        // Act
        notificationRepository.deleteAllById(idsToDelete);
        
        // Assert
        assertFalse(notificationRepository.findById(testNotification.getId()).isPresent());
        assertFalse(notificationRepository.findById(anotherNotification.getId()).isPresent());
        assertTrue(notificationRepository.findById(thirdNotification.getId()).isPresent());
    }
    
    @Test
    void deleteAll_Iterable_ShouldRemoveSpecifiedNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        Notification anotherNotification = new Notification("Another Notification", userId);
        notificationRepository.save(anotherNotification);
        Notification thirdNotification = new Notification("Third Notification", userId);
        notificationRepository.save(thirdNotification);
        
        List<Notification> notificationsToDelete = Arrays.asList(testNotification, anotherNotification);
        
        // Act
        notificationRepository.deleteAll(notificationsToDelete);
        
        // Assert
        assertFalse(notificationRepository.findById(testNotification.getId()).isPresent());
        assertFalse(notificationRepository.findById(anotherNotification.getId()).isPresent());
        assertTrue(notificationRepository.findById(thirdNotification.getId()).isPresent());
    }
    
    @Test
    void deleteAll_ShouldRemoveAllNotifications() {
        // Arrange
        notificationRepository.save(testNotification);
        Notification anotherNotification = new Notification("Another Notification", userId);
        notificationRepository.save(anotherNotification);
        
        // Act
        notificationRepository.deleteAll();
        
        // Assert
        Iterable<Notification> remainingNotifications = notificationRepository.findAll();
        List<Notification> notificationsList = convertToList(remainingNotifications);
        assertTrue(notificationsList.isEmpty());
    }
    
    @Test
    void saveAll_ShouldSaveMultipleNotifications() {
        // Arrange
        Notification anotherNotification = new Notification("Another Notification", userId);
        List<Notification> notificationsToSave = Arrays.asList(testNotification, anotherNotification);
        
        // Act
        Iterable<Notification> savedNotifications = notificationRepository.saveAll(notificationsToSave);
        List<Notification> savedList = convertToList(savedNotifications);
        
        // Assert
        assertEquals(2, savedList.size());
        
        // Проверяем, что уведомления действительно сохранены
        Iterable<Notification> allNotifications = notificationRepository.findAll();
        List<Notification> allList = convertToList(allNotifications);
        assertEquals(2, allList.size());
    }
} 