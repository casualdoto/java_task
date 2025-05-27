package com.example.task_manager.service.impl;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.NotificationService;
import com.example.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    
    @Override
    @CacheEvict(value = {"userTasks", "pendingTasks"}, key = "#task.userId")
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        
        // Создаем уведомление о новой задаче
        Notification notification = new Notification(
                "Новая задача: " + task.getTitle(),
                task.getUserId()
        );
        notificationService.createNotification(notification);
        
        return savedTask;
    }
    
    @Override
    @Cacheable(value = "userTasks", key = "#userId")
    public List<Task> findAllByUserId(UUID userId) {
        return taskRepository.findByUserId(userId);
    }
    
    @Override
    @Cacheable(value = "pendingTasks", key = "#userId")
    public List<Task> findPendingByUserId(UUID userId) {
        return taskRepository.findPendingByUserId(userId);
    }
    
    @Override
    @Cacheable(value = "taskById", key = "#id")
    public Optional<Task> findById(UUID id) {
        return taskRepository.findById(id);
    }
    
    @Override
    @CacheEvict(value = {"userTasks", "pendingTasks", "taskById"}, allEntries = true)
    public void deleteTask(UUID id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            
            // Создаем уведомление об удалении задачи
            Notification notification = new Notification(
                    "Задача удалена: " + task.get().getTitle(),
                    task.get().getUserId()
            );
            notificationService.createNotification(notification);
        }
    }
} 