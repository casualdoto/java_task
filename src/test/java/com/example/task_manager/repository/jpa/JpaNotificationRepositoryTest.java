package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("jpa")
class JpaNotificationRepositoryTest {
    
    @Autowired
    private JpaNotificationRepository notificationRepository;
    
    @Autowired
    private JpaUserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("testuser", "password"));
    }
    
    @Test
    void save_ShouldSaveAndReturnNotification() {
        // Arrange
        Notification notification = new Notification("Test message", testUser.getId());
        
        // Act
        Notification savedNotification = notificationRepository.save(notification);
        
        // Assert
        assertNotNull(savedNotification);
        assertNotNull(savedNotification.getId());
        assertEquals("Test message", savedNotification.getMessage());
    }
    
    @Test
    void findById_WhenNotificationExists_ShouldReturnNotification() {
        // Arrange
        Notification notification = new Notification("Test message", testUser.getId());
        Notification savedNotification = notificationRepository.save(notification);
        
        // Act
        Optional<Notification> foundNotification = notificationRepository.findById(savedNotification.getId());
        
        // Assert
        assertTrue(foundNotification.isPresent());
        assertEquals("Test message", foundNotification.get().getMessage());
    }
    
    @Test
    void findByUserId_ShouldReturnAllUserNotifications() {
        // Arrange
        Notification notification1 = new Notification("Message 1", testUser.getId());
        Notification notification2 = new Notification("Message 2", testUser.getId());
        
        User anotherUser = userRepository.save(new User("another", "password"));
        Notification anotherUserNotification = new Notification("Another user message", anotherUser.getId());
        
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(anotherUserNotification);
        
        // Act
        List<Notification> notifications = notificationRepository.findByUserId(testUser.getId());
        
        // Assert
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> n.getUserId().equals(testUser.getId())));
    }
    
    @Test
    void findByUserIdAndReadFalse_ShouldReturnAllUnreadUserNotifications() {
        // Arrange
        Notification unreadNotification1 = new Notification("Unread 1", testUser.getId());
        Notification unreadNotification2 = new Notification("Unread 2", testUser.getId());
        Notification readNotification = new Notification("Read", testUser.getId());
        readNotification.setRead(true);
        
        notificationRepository.save(unreadNotification1);
        notificationRepository.save(unreadNotification2);
        notificationRepository.save(readNotification);
        
        // Act
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalse(testUser.getId());
        
        // Assert
        assertEquals(2, unreadNotifications.size());
        assertTrue(unreadNotifications.stream().noneMatch(Notification::isRead));
    }
    
    @Test
    void findPendingByUserId_ShouldReturnAllUnreadUserNotifications() {
        // Arrange
        Notification unreadNotification1 = new Notification("Unread 1", testUser.getId());
        Notification unreadNotification2 = new Notification("Unread 2", testUser.getId());
        Notification readNotification = new Notification("Read", testUser.getId());
        readNotification.setRead(true);
        
        notificationRepository.save(unreadNotification1);
        notificationRepository.save(unreadNotification2);
        notificationRepository.save(readNotification);
        
        // Act
        List<Notification> pendingNotifications = notificationRepository.findPendingByUserId(testUser.getId());
        
        // Assert
        assertEquals(2, pendingNotifications.size());
        assertTrue(pendingNotifications.stream().noneMatch(Notification::isRead));
    }
    
    @Test
    void delete_ShouldRemoveNotification() {
        // Arrange
        Notification notification = new Notification("Test message", testUser.getId());
        Notification savedNotification = notificationRepository.save(notification);
        UUID notificationId = savedNotification.getId();
        
        // Act
        notificationRepository.delete(notificationId);
        Optional<Notification> foundNotification = notificationRepository.findById(notificationId);
        
        // Assert
        assertFalse(foundNotification.isPresent());
    }
} 