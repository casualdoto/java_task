package com.example.task_manager.service;

import com.example.task_manager.model.TaskCreatedEvent;
import com.example.task_manager.model.TaskOverdueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.task-created:task-created}")
    private String taskCreatedTopic;
    
    @Value("${app.kafka.topic.task-overdue:task-overdue}")
    private String taskOverdueTopic;

    public void publishTaskCreatedEvent(TaskCreatedEvent event) {
        try {
            log.info("Publishing task created event: {}", event);
            kafkaTemplate.send(taskCreatedTopic, event.getTaskId().toString(), event);
            log.info("Task created event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish task created event: {}", event, e);
            throw new RuntimeException("Failed to publish task created event", e);
        }
    }
    
    public void publishTaskOverdueEvent(TaskOverdueEvent event) {
        try {
            log.info("Publishing task overdue event: {}", event);
            kafkaTemplate.send(taskOverdueTopic, event.getTaskId().toString(), event);
            log.info("Task overdue event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish task overdue event: {}", event, e);
            throw new RuntimeException("Failed to publish task overdue event", e);
        }
    }
} 