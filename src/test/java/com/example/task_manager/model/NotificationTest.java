package com.example.task_manager.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    private Validator validator;
    private Notification notification;
    private UUID userId;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userId = UUID.randomUUID();
        notification = new Notification("Test Notification", userId);
    }

    @Test
    void notificationCreation_WithConstructor_ShouldInitializeFields() {
        // Assert
        assertNotNull(notification.getId());
        assertEquals("Test Notification", notification.getMessage());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreationDate());
        assertEquals(userId, notification.getUserId());
    }

    @Test
    void notificationCreation_WithDefaultConstructor_ShouldInitializeBasicFields() {
        // Act
        Notification defaultNotification = new Notification();
        
        // Assert
        assertNotNull(defaultNotification.getId());
        assertNotNull(defaultNotification.getCreationDate());
        assertFalse(defaultNotification.isRead());
    }

    @Test
    void setId_ShouldChangeId() {
        // Arrange
        UUID newId = UUID.randomUUID();
        
        // Act
        notification.setId(newId);
        
        // Assert
        assertEquals(newId, notification.getId());
    }

    @Test
    void setMessage_ShouldChangeMessage() {
        // Act
        notification.setMessage("New Message");
        
        // Assert
        assertEquals("New Message", notification.getMessage());
    }

    @Test
    void setRead_ShouldChangeReadStatus() {
        // Act
        notification.setRead(true);
        
        // Assert
        assertTrue(notification.isRead());
    }

    @Test
    void setCreationDate_ShouldChangeCreationDate() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        
        // Act
        notification.setCreationDate(newDate);
        
        // Assert
        assertEquals(newDate, notification.getCreationDate());
    }

    @Test
    void setUserId_ShouldChangeUserId() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        
        // Act
        notification.setUserId(newUserId);
        
        // Assert
        assertEquals(newUserId, notification.getUserId());
    }

    @Test
    void validate_WithValidNotification_ShouldHaveNoViolations() {
        // Act
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_WithBlankMessage_ShouldHaveViolation() {
        // Arrange
        notification.setMessage("");
        
        // Act
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasBlankMessageViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Текст уведомления не может быть пустым"));
        assertTrue(hasBlankMessageViolation);
    }

    @Test
    void validate_WithLongMessage_ShouldHaveViolation() {
        // Arrange
        notification.setMessage("a".repeat(501));
        
        // Act
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Текст уведомления не может быть длиннее 500 символов"));
        assertTrue(hasSizeViolation);
    }

    @Test
    void validate_WithNullUserId_ShouldHaveViolation() {
        // Arrange
        notification.setUserId(null);
        
        // Act
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasNotNullViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("ID пользователя должен быть указан"));
        assertTrue(hasNotNullViolation);
    }
} 