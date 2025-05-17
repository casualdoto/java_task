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

class TaskTest {

    private Validator validator;
    private Task task;
    private UUID userId;
    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userId = UUID.randomUUID();
        futureDate = LocalDateTime.now().plusDays(1);
        task = new Task("Test Task", "Test Description", futureDate, userId);
    }

    @Test
    void taskCreation_WithConstructor_ShouldInitializeFields() {
        // Assert
        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertNotNull(task.getCreationDate());
        assertEquals(futureDate, task.getTargetDate());
        assertFalse(task.isDeleted());
        assertEquals(userId, task.getUserId());
    }

    @Test
    void taskCreation_WithDefaultConstructor_ShouldInitializeBasicFields() {
        // Act
        Task defaultTask = new Task();
        
        // Assert
        assertNotNull(defaultTask.getId());
        assertNotNull(defaultTask.getCreationDate());
        assertFalse(defaultTask.isDeleted());
    }

    @Test
    void setId_ShouldChangeId() {
        // Arrange
        UUID newId = UUID.randomUUID();
        
        // Act
        task.setId(newId);
        
        // Assert
        assertEquals(newId, task.getId());
    }

    @Test
    void setTitle_ShouldChangeTitle() {
        // Act
        task.setTitle("New Title");
        
        // Assert
        assertEquals("New Title", task.getTitle());
    }

    @Test
    void setDescription_ShouldChangeDescription() {
        // Act
        task.setDescription("New Description");
        
        // Assert
        assertEquals("New Description", task.getDescription());
    }

    @Test
    void setCreationDate_ShouldChangeCreationDate() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        
        // Act
        task.setCreationDate(newDate);
        
        // Assert
        assertEquals(newDate, task.getCreationDate());
    }

    @Test
    void setTargetDate_ShouldChangeTargetDate() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        
        // Act
        task.setTargetDate(newDate);
        
        // Assert
        assertEquals(newDate, task.getTargetDate());
    }

    @Test
    void setDeleted_ShouldChangeDeletedStatus() {
        // Act
        task.setDeleted(true);
        
        // Assert
        assertTrue(task.isDeleted());
    }

    @Test
    void setUserId_ShouldChangeUserId() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        
        // Act
        task.setUserId(newUserId);
        
        // Assert
        assertEquals(newUserId, task.getUserId());
    }

    @Test
    void validate_WithValidTask_ShouldHaveNoViolations() {
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_WithBlankTitle_ShouldHaveViolation() {
        // Arrange
        task.setTitle("");
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasBlankTitleViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Заголовок задачи не может быть пустым"));
        assertTrue(hasBlankTitleViolation);
    }

    @Test
    void validate_WithShortTitle_ShouldHaveViolation() {
        // Arrange
        task.setTitle("ab");
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Заголовок должен содержать от 3 до 100 символов"));
        assertTrue(hasSizeViolation);
    }

    @Test
    void validate_WithLongTitle_ShouldHaveViolation() {
        // Arrange
        task.setTitle("a".repeat(101));
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Заголовок должен содержать от 3 до 100 символов"));
        assertTrue(hasSizeViolation);
    }

    @Test
    void validate_WithLongDescription_ShouldHaveViolation() {
        // Arrange
        task.setDescription("a".repeat(1001));
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Описание не может быть длиннее 1000 символов"));
        assertTrue(hasSizeViolation);
    }

    @Test
    void validate_WithNullTargetDate_ShouldHaveViolation() {
        // Arrange
        task.setTargetDate(null);
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasNotNullViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Плановая дата должна быть указана"));
        assertTrue(hasNotNullViolation);
    }

    @Test
    void validate_WithPastTargetDate_ShouldHaveViolation() {
        // Arrange
        task.setTargetDate(LocalDateTime.now().minusDays(1));
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasFutureViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Плановая дата должна быть в будущем"));
        assertTrue(hasFutureViolation);
    }

    @Test
    void validate_WithNullUserId_ShouldHaveViolation() {
        // Arrange
        task.setUserId(null);
        
        // Act
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Assert
        assertFalse(violations.isEmpty());
        boolean hasNotNullViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("ID пользователя должен быть указан"));
        assertTrue(hasNotNullViolation);
    }
} 