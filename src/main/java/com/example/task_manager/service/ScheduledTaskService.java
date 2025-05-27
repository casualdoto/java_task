package com.example.task_manager.service;

import com.example.task_manager.model.Task;
import com.example.task_manager.model.TaskOverdueEvent;
import com.example.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    
    private final TaskRepository taskRepository;
    private final KafkaMessageProducer kafkaMessageProducer;
    
    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void checkOverdueTasks() {
        log.info("Запуск проверки просроченных задач");
        
        List<Task> overdueTasks = taskRepository.findOverdueTasks();
        
        if (!overdueTasks.isEmpty()) {
            log.info("Найдено {} просроченных задач", overdueTasks.size());
            processOverdueTasksAsync(overdueTasks);
        } else {
            log.info("Просроченных задач не найдено");
        }
    }
    
    @Async("taskExecutor")
    public void processOverdueTasksAsync(List<Task> overdueTasks) {
        log.info("Асинхронная обработка {} просроченных задач", overdueTasks.size());
        
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : overdueTasks) {
            TaskOverdueEvent event = new TaskOverdueEvent(
                    task.getId(),
                    task.getTitle(),
                    task.getUserId(),
                    task.getTargetDate(),
                    now
            );
            
            kafkaMessageProducer.publishTaskOverdueEvent(event);
            log.debug("Отправлено событие о просроченной задаче: {}", task.getTitle());
        }
        
        log.info("Завершена асинхронная обработка просроченных задач");
    }
} 