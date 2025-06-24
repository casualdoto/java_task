package com.example.task_manager.service.impl;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.Task;
import com.example.task_manager.model.TaskCreatedEvent;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.KafkaMessageProducer;
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
    private final KafkaMessageProducer kafkaMessageProducer;
    
    @Override
    @CacheEvict(value = {"userTasks", "pendingTasks"}, key = "#task.userId")
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        
        // Публикуем событие в Kafka вместо прямого создания уведомления
        TaskCreatedEvent event = new TaskCreatedEvent(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getUserId(),
                savedTask.getCreationDate(),
                savedTask.getTargetDate()
        );
        kafkaMessageProducer.publishTaskCreatedEvent(event);
        
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
            
            // Для удаления пока оставляем прямое создание уведомления
            // В будущем можно создать отдельное событие TaskDeletedEvent
            Notification notification = new Notification(
                    "Задача удалена: " + task.get().getTitle(),
                    task.get().getUserId()
            );
            notificationService.createNotification(notification);
        }
    }
} 