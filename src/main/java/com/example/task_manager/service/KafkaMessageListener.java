package com.example.task_manager.service;

import com.example.task_manager.model.Notification;
import com.example.task_manager.model.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topic.task-created:task-created}", groupId = "${spring.kafka.consumer.group-id:task-manager-group}")
    public void handleTaskCreatedEvent(TaskCreatedEvent event) {
        try {
            log.info("Received task created event: {}", event);
            
            // Создаем уведомление о новой задаче
            Notification notification = new Notification(
                    "Новая задача: " + event.getTitle(),
                    event.getUserId()
            );
            
            notificationService.createNotification(notification);
            log.info("Notification created for task: {}", event.getTaskId());
            
        } catch (Exception e) {
            log.error("Failed to process task created event: {}", event, e);
            // В реальном приложении здесь можно добавить retry логику или DLQ
        }
    }
} 