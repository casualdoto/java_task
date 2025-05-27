package com.example.task_manager.service;

import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private KafkaMessageProducer kafkaMessageProducer;
    
    private ScheduledTaskService scheduledTaskService;

    @BeforeEach
    void setUp() {
        scheduledTaskService = new ScheduledTaskService(taskRepository, kafkaMessageProducer);
    }

    @Test
    void checkOverdueTasks_WhenNoOverdueTasks_ShouldNotProcessAny() {
        // Arrange
        when(taskRepository.findOverdueTasks()).thenReturn(Collections.emptyList());

        // Act
        scheduledTaskService.checkOverdueTasks();

        // Assert
        verify(taskRepository).findOverdueTasks();
        verifyNoInteractions(kafkaMessageProducer);
    }

    @Test
    void checkOverdueTasks_WhenOverdueTasksExist_ShouldProcessThem() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Task overdueTask1 = new Task("Task 1", "Description 1", 
                LocalDateTime.now().minusDays(1), userId);
        Task overdueTask2 = new Task("Task 2", "Description 2", 
                LocalDateTime.now().minusHours(2), userId);
        
        List<Task> overdueTasks = Arrays.asList(overdueTask1, overdueTask2);
        when(taskRepository.findOverdueTasks()).thenReturn(overdueTasks);

        // Act
        scheduledTaskService.checkOverdueTasks();

        // Assert
        verify(taskRepository).findOverdueTasks();
        verify(kafkaMessageProducer, times(2)).publishTaskOverdueEvent(any());
    }

    @Test
    void processOverdueTasksAsync_ShouldProcessAllTasks() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Task overdueTask1 = new Task("Task 1", "Description 1", 
                LocalDateTime.now().minusDays(1), userId);
        Task overdueTask2 = new Task("Task 2", "Description 2", 
                LocalDateTime.now().minusHours(2), userId);
        
        List<Task> overdueTasks = Arrays.asList(overdueTask1, overdueTask2);

        // Act
        scheduledTaskService.processOverdueTasksAsync(overdueTasks);

        // Assert
        verify(kafkaMessageProducer, times(2)).publishTaskOverdueEvent(any());
    }

    @Test
    void processOverdueTasksAsync_WhenKafkaFails_ShouldContinueProcessing() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Task overdueTask1 = new Task("Task 1", "Description 1", 
                LocalDateTime.now().minusDays(1), userId);
        Task overdueTask2 = new Task("Task 2", "Description 2", 
                LocalDateTime.now().minusHours(2), userId);
        
        List<Task> overdueTasks = Arrays.asList(overdueTask1, overdueTask2);
        
        // Первый вызов бросает исключение, второй проходит нормально
        doThrow(new RuntimeException("Kafka error"))
            .doNothing()
            .when(kafkaMessageProducer).publishTaskOverdueEvent(any());

        // Act & Assert
        assertDoesNotThrow(() -> scheduledTaskService.processOverdueTasksAsync(overdueTasks));
        
        // Проверяем, что оба события были обработаны
        verify(kafkaMessageProducer, times(2)).publishTaskOverdueEvent(any());
    }

    @Test
    void processOverdueTasksAsync_WithEmptyList_ShouldNotCallKafka() {
        // Act
        scheduledTaskService.processOverdueTasksAsync(Collections.emptyList());

        // Assert
        verifyNoInteractions(kafkaMessageProducer);
    }
} 