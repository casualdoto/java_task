package com.example.task_manager.config;

import com.example.task_manager.model.Task;
import com.example.task_manager.model.User;
import com.example.task_manager.service.TaskService;
import com.example.task_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@Profile("dev")
public class DataInitializer {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TaskService taskService;
    
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Создаем тестового пользователя
            User user = new User("test", "password");
            user = userService.register(user);
            
            // Создаем тестовые задачи
            createTestTask(user.getId(), "Задача 1", "Описание задачи 1", LocalDateTime.now().plusDays(1));
            createTestTask(user.getId(), "Задача 2", "Описание задачи 2", LocalDateTime.now().plusDays(2));
            createTestTask(user.getId(), "Задача 3", "Описание задачи 3", LocalDateTime.now().plusDays(3));
            
            System.out.println("Тестовые данные созданы.");
        };
    }
    
    private void createTestTask(UUID userId, String title, String description, LocalDateTime targetDate) {
        Task task = new Task(title, description, targetDate, userId);
        taskService.createTask(task);
    }
} 