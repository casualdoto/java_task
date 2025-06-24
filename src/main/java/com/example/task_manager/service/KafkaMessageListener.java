package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.TaskCreatedEvent;
import com.example.task_manager.model.TaskOverdueEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topic.task-created:task-created}", groupId = "${spring.kafka.consumer.group-id:task-manager-group}")
    public void handleTaskCreatedEvent(Object eventData) {
        try {
            TaskCreatedEvent event = objectMapper.convertValue(eventData, TaskCreatedEvent.class);
            log.info("Received task created event: {}", event);
            
            // Создаем уведомление о новой задаче
            Notification notification = new Notification(
                    "Новая задача: " + event.getTitle(),
                    event.getUserId()
            );
            
            notificationService.createNotification(notification);
            log.info("Notification created for task: {}", event.getTaskId());
            
        } catch (Exception e) {
            log.error("Failed to process task created event: {}", eventData, e);
            // В реальном приложении здесь можно добавить retry логику или DLQ
        }
    }
    
    @KafkaListener(topics = "${app.kafka.topic.task-overdue:task-overdue}", groupId = "${spring.kafka.consumer.group-id:task-manager-group}")
    public void handleTaskOverdueEvent(Object eventData) {
        try {
            TaskOverdueEvent event = objectMapper.convertValue(eventData, TaskOverdueEvent.class);
            log.info("Received task overdue event: {}", event);
            
            // Создаем уведомление о просроченной задаче
            Notification notification = new Notification(
                    "Задача просрочена: " + event.getTitle() + " (срок: " + event.getTargetDate() + ")",
                    event.getUserId()
            );
            
            notificationService.createNotification(notification);
            log.info("Overdue notification created for task: {}", event.getTaskId());
            
        } catch (Exception e) {
            log.error("Failed to process task overdue event: {}", eventData, e);
        }
    }
} 