package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    @Test
    void existsById_WhenTaskExists_ShouldReturnTrue() {
        // Arrange
        taskRepository.save(testTask);
        
        // Act
        boolean exists = taskRepository.existsById(taskId);
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void existsById_WhenTaskDoesNotExist_ShouldReturnFalse() {
        // Act
        boolean exists = taskRepository.existsById(UUID.randomUUID());
        
        // Assert
        assertFalse(exists);
    }
    
    @Test
    void count_ShouldReturnNumberOfTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        
        // Act
        long count = taskRepository.count();
        
        // Assert
        assertEquals(2, count);
    }
    
    @Test
    void deleteAll_ShouldRemoveAllTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        
        // Act
        taskRepository.deleteAll();
        
        // Assert
        assertEquals(0, convertToList(taskRepository.findAll()).size());
    }
    
    @Test
    void saveAll_ShouldSaveMultipleTasks() {
        // Arrange
        List<Task> tasksToSave = new ArrayList<>();
        tasksToSave.add(testTask);
        
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        tasksToSave.add(anotherTask);
        
        // Act
        Iterable<Task> savedTasks = taskRepository.saveAll(tasksToSave);
        List<Task> savedTasksList = convertToList(savedTasks);
        
        // Assert
        assertEquals(2, savedTasksList.size());
        assertEquals(2, convertToList(taskRepository.findAll()).size());
    }
    
    private <T> List<T> convertToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
    
    @Test
    void findAllByDeletedFalse_ShouldReturnOnlyNonDeletedTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        
        Task deletedTask = new Task("Deleted Task", "Description", LocalDateTime.now().plusDays(3), userId);
        taskRepository.save(deletedTask);
        taskRepository.deleteById(deletedTask.getId());
        
        // Act
        List<Task> nonDeletedTasks = taskRepository.findAllByDeletedFalse();
        
        // Assert
        assertEquals(2, nonDeletedTasks.size());
        for (Task task : nonDeletedTasks) {
            assertFalse(task.isDeleted());
        }
    }
    
    @Test
    void findByUserIdAndDeletedFalse_ShouldReturnOnlyUserNonDeletedTasks() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        
        // Создаем задачу для другого пользователя
        UUID anotherUserId = UUID.randomUUID();
        Task anotherUserTask = new Task("Another User Task", "Description", LocalDateTime.now().plusDays(3), anotherUserId);
        taskRepository.save(anotherUserTask);
        
        // Создаем и удаляем задачу текущего пользователя
        Task deletedTask = new Task("Deleted Task", "Description", LocalDateTime.now().plusDays(3), userId);
        taskRepository.save(deletedTask);
        taskRepository.deleteById(deletedTask.getId());
        
        // Act
        List<Task> userNonDeletedTasks = taskRepository.findByUserIdAndDeletedFalse(userId);
        
        // Assert
        assertEquals(2, userNonDeletedTasks.size());
        for (Task task : userNonDeletedTasks) {
            assertEquals(userId, task.getUserId());
            assertFalse(task.isDeleted());
        }
    }
    
    @Test
    void deleteAllById_ShouldMarkMultipleTasksAsDeleted() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        
        List<UUID> idsToDelete = Arrays.asList(testTask.getId(), anotherTask.getId());
        
        // Act
        taskRepository.deleteAllById(idsToDelete);
        
        // Assert
        Optional<Task> deletedTask1 = taskRepository.findById(testTask.getId());
        Optional<Task> deletedTask2 = taskRepository.findById(anotherTask.getId());
        
        assertTrue(deletedTask1.isPresent());
        assertTrue(deletedTask1.get().isDeleted());
        assertTrue(deletedTask2.isPresent());
        assertTrue(deletedTask2.get().isDeleted());
    }
    
    @Test
    void deleteAll_Iterable_ShouldMarkSpecifiedTasksAsDeleted() {
        // Arrange
        taskRepository.save(testTask);
        Task anotherTask = new Task("Another Task", "Description", LocalDateTime.now().plusDays(2), userId);
        taskRepository.save(anotherTask);
        Task thirdTask = new Task("Third Task", "Description", LocalDateTime.now().plusDays(3), userId);
        taskRepository.save(thirdTask);
        
        List<Task> tasksToDelete = Arrays.asList(testTask, anotherTask);
        
        // Act
        taskRepository.deleteAll(tasksToDelete);
        
        // Assert
        Optional<Task> deletedTask1 = taskRepository.findById(testTask.getId());
        Optional<Task> deletedTask2 = taskRepository.findById(anotherTask.getId());
        Optional<Task> nonDeletedTask = taskRepository.findById(thirdTask.getId());
        
        assertTrue(deletedTask1.isPresent());
        assertTrue(deletedTask1.get().isDeleted());
        assertTrue(deletedTask2.isPresent());
        assertTrue(deletedTask2.get().isDeleted());
        assertTrue(nonDeletedTask.isPresent());
        assertFalse(nonDeletedTask.get().isDeleted());
    }
} 