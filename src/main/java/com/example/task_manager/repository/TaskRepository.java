package com.example.task_manager.repository;

import com.example.task_manager.model.Task;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends Repository<Task> {
    List<Task> findByUserIdAndDeletedFalse(UUID userId);
    List<Task> findAllByDeletedFalse();
    List<Task> findByUserId(UUID userId);
    List<Task> findPendingByUserId(UUID userId);
    
    @Query("SELECT t FROM Task t WHERE t.targetDate < CURRENT_TIMESTAMP AND t.deleted = false")
    List<Task> findOverdueTasks();
} 