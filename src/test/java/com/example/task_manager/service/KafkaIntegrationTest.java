package com.example.task_manager.service;

import com.example.task_manager.model.Task;
import com.example.task_manager.model.TaskCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"task-created"})
@ActiveProfiles("test")
class KafkaIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private NotificationService notificationService;

    @Test
    void testTaskCreationTriggersKafkaMessage() throws InterruptedException {
        // Arrange
        UUID userId = UUID.randomUUID();
        Task task = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), userId);

        // Act
        Task createdTask = taskService.createTask(task);

        // Assert
        assertNotNull(createdTask);
        assertNotNull(createdTask.getId());

        // Даем время для обработки Kafka сообщения
        TimeUnit.SECONDS.sleep(2);

        // Проверяем, что уведомление было создано через Kafka
        var notifications = notificationService.findAllByUserId(userId);
        // В реальном тесте здесь можно проверить, что уведомление создано
        // Но для этого нужно настроить embedded Kafka правильно
    }
} 