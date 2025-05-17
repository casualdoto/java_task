package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task testTask;
    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testTask = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), userId);
        testTask.setId(taskId);
    }

    @Test
    void createTask_ShouldSaveTaskAndCreateNotification() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(new Notification());

        // Act
        Task result = taskService.createTask(testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getUserId(), result.getUserId());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void findAllByUserId_ShouldReturnUserTasks() {
        // Arrange
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        anotherTask.setId(UUID.randomUUID());
        List<Task> tasks = Arrays.asList(testTask, anotherTask);
        when(taskRepository.findByUserId(userId)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findByUserId(userId);
    }

    @Test
    void findPendingByUserId_ShouldReturnPendingTasks() {
        // Arrange
        Task pendingTask = new Task("Pending Task", "Pending Description", LocalDateTime.now().plusDays(3), userId);
        pendingTask.setId(UUID.randomUUID());
        List<Task> pendingTasks = Arrays.asList(testTask, pendingTask);
        when(taskRepository.findPendingByUserId(userId)).thenReturn(pendingTasks);

        // Act
        List<Task> result = taskService.findPendingByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findPendingByUserId(userId);
    }

    @Test
    void findById_WhenTaskExists_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act
        Optional<Task> result = taskService.findById(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTask.getId(), result.get().getId());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void findById_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Task> result = taskService.findById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldMarkAsDeletedAndCreateNotification() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(notificationService.createNotification(any(Notification.class))).thenReturn(new Notification());

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(taskId);
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        taskService.deleteTask(nonExistentId);

        // Assert
        verify(taskRepository, times(1)).findById(nonExistentId);
        verify(taskRepository, never()).delete(any(UUID.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }
} 