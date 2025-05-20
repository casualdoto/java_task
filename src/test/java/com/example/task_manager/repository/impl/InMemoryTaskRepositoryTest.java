package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {

    private InMemoryTaskRepository taskRepository;
    private Task testTask;
    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        userId = UUID.randomUUID();
        testTask = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), userId);
        taskId = testTask.getId();
    }

    @Test
    void save_ShouldSaveAndReturnTask() {
        // Act
        Task savedTask = taskRepository.save(testTask);

        // Assert
        assertNotNull(savedTask);
        assertEquals(testTask.getTitle(), savedTask.getTitle());
        assertEquals(testTask.getDescription(), savedTask.getDescription());
    }

    @Test
    void findById_WhenTaskExists_ShouldReturnTask() {
        // Arrange
        taskRepository.save(testTask);

        // Act
        Optional<Task> foundTask = taskRepository.findById(taskId);

        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals(testTask.getTitle(), foundTask.get().getTitle());
    }

    @Test
    void findById_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Task> foundTask = taskRepository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundTask.isPresent());
    }

    @Test
    void findAll_WhenNoTasks_ShouldReturnEmptyList() {
        // Act
        Iterable<Task> tasksIterable = taskRepository.findAll();
        List<Task> tasks = convertToList(tasksIterable);

        // Assert
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    void findAll_WhenTasksExist_ShouldReturnAllTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);

        // Act
        Iterable<Task> tasksIterable = taskRepository.findAll();
        List<Task> tasks = convertToList(tasksIterable);

        // Assert
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void delete_ShouldMarkTaskAsDeleted() {
        // Arrange
        taskRepository.save(testTask);

        // Act
        taskRepository.deleteById(taskId);
        Optional<Task> deletedTask = taskRepository.findById(taskId);

        // Assert
        assertTrue(deletedTask.isPresent());
        assertTrue(deletedTask.get().isDeleted());
    }

    @Test
    void delete_WhenTaskDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> taskRepository.deleteById(nonExistentId));
    }

    @Test
    void findByUserId_ShouldReturnUserTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        
        // Create a task for another user
        UUID anotherUserId = UUID.randomUUID();
        Task anotherUserTask = new Task("Another User Task", "Another User Description", LocalDateTime.now().plusDays(3), anotherUserId);
        taskRepository.save(anotherUserTask);

        // Act
        List<Task> userTasks = taskRepository.findByUserId(userId);

        // Assert
        assertNotNull(userTasks);
        assertEquals(2, userTasks.size());
        for (Task task : userTasks) {
            assertEquals(userId, task.getUserId());
        }
    }

    @Test
    void findByUserId_ShouldNotReturnDeletedTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task deletedTask = new Task("Deleted Task", "Deleted Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(deletedTask);
        taskRepository.deleteById(deletedTask.getId());

        // Act
        List<Task> userTasks = taskRepository.findByUserId(userId);

        // Assert
        assertNotNull(userTasks);
        assertEquals(1, userTasks.size());
        assertEquals(testTask.getId(), userTasks.get(0).getId());
    }

    @Test
    void findPendingByUserId_ShouldReturnPendingTasks() {
        // Arrange
        taskRepository.save(testTask);
        
        // Create a task in the past
        Task pastTask = new Task("Past Task", "Past Description", LocalDateTime.now().minusDays(1), userId);
        taskRepository.save(pastTask);

        // Act
        List<Task> pendingTasks = taskRepository.findPendingByUserId(userId);

        // Assert
        assertNotNull(pendingTasks);
        assertEquals(1, pendingTasks.size());
        assertEquals(testTask.getId(), pendingTasks.get(0).getId());
    }

    @Test
    void findPendingByUserId_ShouldNotReturnDeletedTasks() {
        // Arrange
        taskRepository.save(testTask);
        
        Task deletedTask = new Task("Deleted Task", "Deleted Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(deletedTask);
        taskRepository.deleteById(deletedTask.getId());

        // Act
        List<Task> pendingTasks = taskRepository.findPendingByUserId(userId);

        // Assert
        assertNotNull(pendingTasks);
        assertEquals(1, pendingTasks.size());
        assertEquals(testTask.getId(), pendingTasks.get(0).getId());
    }
    
    private <T> List<T> convertToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
} 