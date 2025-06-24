package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.impl.InMemoryNotificationRepository;
import com.example.task_manager.repository.impl.InMemoryTaskRepository;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import com.example.task_manager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private NotificationService notificationService;
    private TaskService taskService;

    private Task testTask;
    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        // Создаем репозитории и сервисы
        taskRepository = new InMemoryTaskRepository();
        var notificationRepository = new InMemoryNotificationRepository();
        notificationService = new NotificationServiceImpl(notificationRepository);
        taskService = new TaskServiceImpl(taskRepository, notificationService);
        
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testTask = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), userId);
        testTask.setId(taskId);
    }

    @Test
    void createTask_ShouldSaveTaskAndCreateNotification() {
        // Act
        Task result = taskService.createTask(testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getUserId(), result.getUserId());
        
        // Проверяем, что задача сохранена в репозитории
        Optional<Task> savedTask = taskRepository.findById(result.getId());
        assertTrue(savedTask.isPresent());
        assertEquals(testTask.getTitle(), savedTask.get().getTitle());
        
        // Проверяем, что уведомление создано
        List<Notification> notifications = notificationService.findAllByUserId(userId);
        assertEquals(1, notifications.size());
        assertTrue(notifications.get(0).getMessage().contains(testTask.getTitle()));
    }

    @Test
    void findAllByUserId_ShouldReturnUserTasks() {
        // Arrange
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        anotherTask.setId(UUID.randomUUID());
        
        taskRepository.save(testTask);
        taskRepository.save(anotherTask);
        
        // Добавляем задачу другого пользователя, которая не должна быть возвращена
        UUID anotherUserId = UUID.randomUUID();
        Task otherUserTask = new Task("Other User Task", "Other Description", LocalDateTime.now().plusDays(1), anotherUserId);
        taskRepository.save(otherUserTask);

        // Act
        List<Task> result = taskService.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getTitle().equals(testTask.getTitle())));
        assertTrue(result.stream().anyMatch(t -> t.getTitle().equals(anotherTask.getTitle())));
        assertFalse(result.stream().anyMatch(t -> t.getTitle().equals(otherUserTask.getTitle())));
    }

    @Test
    void findPendingByUserId_ShouldReturnPendingTasks() {
        // Arrange
        taskRepository.save(testTask);
        
        // Создаем задачу в прошлом (которая не должна считаться ожидающей)
        Task pastTask = new Task("Past Task", "Past Description", LocalDateTime.now().minusDays(1), userId);
        taskRepository.save(pastTask);
        
        // Создаем удаленную задачу (которая не должна считаться ожидающей)
        Task deletedTask = new Task("Deleted Task", "Deleted Description", LocalDateTime.now().plusDays(3), userId);
        deletedTask.setDeleted(true);
        taskRepository.save(deletedTask);

        // Act
        List<Task> result = taskService.findPendingByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
    }

    @Test
    void findById_WhenTaskExists_ShouldReturnTask() {
        // Arrange
        taskRepository.save(testTask);

        // Act
        Optional<Task> result = taskService.findById(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTask.getId(), result.get().getId());
    }

    @Test
    void findById_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Task> result = taskService.findById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldMarkAsDeletedAndCreateNotification() {
        // Arrange
        taskRepository.save(testTask);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        Optional<Task> foundTask = taskRepository.findById(taskId);
        assertTrue(foundTask.isPresent());
        assertTrue(foundTask.get().isDeleted());
        
        // Проверяем, что было создано уведомление о удалении
        List<Notification> notifications = notificationService.findAllByUserId(userId);
        assertEquals(1, notifications.size());
        assertTrue(notifications.get(0).getMessage().contains("удалена"));
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        taskService.deleteTask(nonExistentId);

        // Assert
        // Никаких ошибок быть не должно, сервис должен просто ничего не сделать
        
        // Проверяем, что уведомление не было создано
        List<Notification> notifications = notificationService.findAllByUserId(userId);
        assertTrue(notifications.isEmpty());
    }
} 