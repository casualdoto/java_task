package com.example.task_manager.integration;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.Task;
import com.example.task_manager.model.User;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.repository.impl.InMemoryTaskRepository;
import com.example.task_manager.repository.impl.InMemoryUserRepository;
import com.example.task_manager.service.NotificationService;
import com.example.task_manager.service.TaskService;
import com.example.task_manager.service.UserService;
import com.example.task_manager.service.impl.NotificationServiceImpl;
import com.example.task_manager.service.impl.TaskServiceImpl;
import com.example.task_manager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceIntegrationTest {

    private UserService userService;
    private TaskService taskService;
    private NotificationService notificationService;

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    
    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Создаем репозитории
        userRepository = new InMemoryUserRepository();
        taskRepository = new InMemoryTaskRepository();
        var notificationRepository = new com.example.task_manager.repository.impl.InMemoryNotificationRepository();

        // Создаем сервисы
        userService = new UserServiceImpl(userRepository);
        notificationService = new NotificationServiceImpl(notificationRepository);
        taskService = new TaskServiceImpl(taskRepository, notificationService);

        // Создаем тестового пользователя
        testUser = new User("testUser", "password123");
        testUser = userService.register(testUser);

        // Создаем тестовую задачу
        testTask = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), testUser.getId());
    }

    @Test
    void createTask_ShouldSaveTaskAndCreateNotification() {
        // Act
        Task savedTask = taskService.createTask(testTask);

        // Assert
        assertNotNull(savedTask);
        assertEquals(testTask.getTitle(), savedTask.getTitle());
        assertEquals(testTask.getUserId(), savedTask.getUserId());

        // Проверяем, что задача была сохранена в репозитории
        Optional<Task> foundTask = taskService.findById(savedTask.getId());
        assertTrue(foundTask.isPresent());
        assertEquals(savedTask.getId(), foundTask.get().getId());

        // Проверяем, что уведомление было создано
        List<Notification> notifications = notificationService.findAllByUserId(testUser.getId());
        assertEquals(1, notifications.size());
        assertTrue(notifications.get(0).getMessage().contains(testTask.getTitle()));
    }

    @Test
    void findAllByUserId_ShouldReturnUserTasks() {
        // Arrange
        taskService.createTask(testTask);
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), testUser.getId());
        taskService.createTask(anotherTask);

        // Act
        List<Task> userTasks = taskService.findAllByUserId(testUser.getId());

        // Assert
        assertEquals(2, userTasks.size());
        assertTrue(userTasks.stream().anyMatch(task -> task.getTitle().equals(testTask.getTitle())));
        assertTrue(userTasks.stream().anyMatch(task -> task.getTitle().equals(anotherTask.getTitle())));
    }

    @Test
    void deleteTask_ShouldMarkTaskAsDeletedAndCreateNotification() {
        // Arrange
        Task savedTask = taskService.createTask(testTask);
        
        // Очищаем предыдущие уведомления для простоты проверки
        List<Notification> notifications = notificationService.findAllByUserId(testUser.getId());
        notifications.forEach(n -> notificationService.markAsRead(n.getId()));

        // Act
        taskService.deleteTask(savedTask.getId());

        // Assert
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertTrue(foundTask.isPresent()); // Задача должна существовать, но быть помеченной как удаленная
        assertTrue(foundTask.get().isDeleted());

        // Проверяем, что было создано уведомление о удалении
        List<Notification> newNotifications = notificationService.findAllByUserId(testUser.getId());
        assertFalse(newNotifications.isEmpty());
        assertTrue(newNotifications.stream()
                .anyMatch(n -> n.getMessage().contains("удалена")));
    }

    @Test
    void findPendingByUserId_ShouldReturnOnlyPendingTasks() {
        // Arrange
        taskService.createTask(testTask);
        
        // Создаем задачу в прошлом (которая не должна считаться ожидающей)
        Task pastTask = new Task("Past Task", "Past Description", LocalDateTime.now().minusDays(1), testUser.getId());
        taskRepository.save(pastTask);

        // Создаем задачу, которую потом удалим
        Task deletedTask = new Task("Deleted Task", "Deleted Description", LocalDateTime.now().plusDays(3), testUser.getId());
        deletedTask = taskService.createTask(deletedTask);
        taskService.deleteTask(deletedTask.getId());

        // Act
        List<Task> pendingTasks = taskService.findPendingByUserId(testUser.getId());

        // Assert
        assertEquals(1, pendingTasks.size());
        assertEquals(testTask.getTitle(), pendingTasks.get(0).getTitle());
    }
} 