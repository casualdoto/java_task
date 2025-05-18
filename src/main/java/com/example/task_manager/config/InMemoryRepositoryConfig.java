package com.example.task_manager.config;

import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.repository.impl.InMemoryNotificationRepository;
import com.example.task_manager.repository.impl.InMemoryTaskRepository;
import com.example.task_manager.repository.impl.InMemoryUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("inmemory")
public class InMemoryRepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }

    @Bean
    public NotificationRepository notificationRepository() {
        return new InMemoryNotificationRepository();
    }
} 