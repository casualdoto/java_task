package com.example.task_manager.repository;

import com.example.task_manager.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends Repository<Task> {
    List<Task> findByUserIdAndDeletedFalse(UUID userId);
    List<Task> findAllByDeletedFalse();
} 