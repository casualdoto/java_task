package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.Task;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import com.example.task_manager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    
    private TaskService taskService;

    private Task testTask;
    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, notificationService);
        
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testTask = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), userId);
        testTask.setId(taskId);
    }

    @Test
    void createTask_ShouldSaveTaskAndCreateNotification() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // Act
        Task result = taskService.createTask(testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getUserId(), result.getUserId());
        
        // Проверяем, что задача сохранена в репозитории
        verify(taskRepository).save(any(Task.class));
        
        // Проверяем, что уведомление создано
        verify(notificationService).createNotification(any(Notification.class));
    }

    @Test
    void findAllByUserId_ShouldReturnUserTasks() {
        // Arrange
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        anotherTask.setId(UUID.randomUUID());
        
        List<Task> mockTasks = Arrays.asList(testTask, anotherTask);
        
        // Мокируем метод findByUserId, который вызывается в TaskServiceImpl.findAllByUserId
        when(taskRepository.findByUserId(userId)).thenReturn(mockTasks);

        // Act
        List<Task> result = taskService.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockTasks.size(), result.size(), "Размер списка должен быть равен 2");
        
        // Проверяем, что в списке содержатся нужные задачи
        assertTrue(result.contains(testTask), "Результат должен содержать testTask");
        assertTrue(result.contains(anotherTask), "Результат должен содержать anotherTask");
        
        // Проверяем, что метод репозитория был вызван с правильным аргументом
        verify(taskRepository).findByUserId(userId);
    }

    @Test
    void findPendingByUserId_ShouldReturnPendingTasks() {
        // Arrange
        List<Task> mockTasks = List.of(testTask);
        when(taskRepository.findPendingByUserId(userId)).thenReturn(mockTasks);

        // Act
        List<Task> result = taskService.findPendingByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
        
        verify(taskRepository).findPendingByUserId(userId);
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
        
        verify(taskRepository).findById(taskId);
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
        
        verify(taskRepository).findById(nonExistentId);
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldMarkAsDeletedAndCreateNotification() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        
        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository).deleteById(taskId);
        
        // Проверяем, что было создано уведомление о удалении
        verify(notificationService).createNotification(any(Notification.class));
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        taskService.deleteTask(nonExistentId);

        // Assert
        verify(taskRepository).findById(nonExistentId);
        verifyNoMoreInteractions(taskRepository);
        
        // Проверяем, что уведомление не было создано
        verifyNoInteractions(notificationService);
    }
} 