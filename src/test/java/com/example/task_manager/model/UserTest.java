package com.example.task_manager.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User("testuser", "password123");
    }

    @Test
    void userCreation_WithConstructor_ShouldInitializeFields() {
        // Assert
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void userCreation_WithDefaultConstructor_ShouldInitializeIdOnly() {
        // Act
        User defaultUser = new User();
        
        // Assert
        assertNotNull(defaultUser.getId());
        assertNull(defaultUser.getUsername());
        assertNull(defaultUser.getPassword());
    }

    @Test
    void setId_ShouldChangeId() {
        // Arrange
        UUID newId = UUID.randomUUID();
        
        // Act
        user.setId(newId);
        
        // Assert
        assertEquals(newId, user.getId());
    }

    @Test
    void setUsername_ShouldChangeUsername() {
        // Act
        user.setUsername("newUsername");
        
        // Assert
        assertEquals("newUsername", user.getUsername());
    }

    @Test
    void setPassword_ShouldChangePassword() {
        // Act
        user.setPassword("newPassword");
        
        // Assert
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void validate_WithValidUser_ShouldHaveNoViolations() {
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_WithBlankUsername_ShouldHaveViolation() {
        // Arrange
        user.setUsername("");
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasBlankUsernameViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Имя пользователя не может быть пустым"));
        assertTrue(hasBlankUsernameViolation);
    }

    @Test
    void validate_WithShortUsername_ShouldHaveViolation() {
        // Arrange
        user.setUsername("ab");
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Имя пользователя должно содержать от 3 до 50 символов"));
        assertTrue(hasSizeViolation);
    }

    @Test
    void validate_WithLongUsername_ShouldHaveViolation() {
        // Arrange
        user.setUsername("a".repeat(51));
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Имя пользователя должно содержать от 3 до 50 символов"));
        assertTrue(hasSizeViolation);
    }

    @Test
    void validate_WithBlankPassword_ShouldHaveViolation() {
        // Arrange
        user.setPassword("");
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasBlankPasswordViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Пароль не может быть пустым"));
        assertTrue(hasBlankPasswordViolation);
    }

    @Test
    void validate_WithShortPassword_ShouldHaveViolation() {
        // Arrange
        user.setPassword("12345");
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Пароль должен содержать минимум 6 символов"));
        assertTrue(hasSizeViolation);
    }
} 