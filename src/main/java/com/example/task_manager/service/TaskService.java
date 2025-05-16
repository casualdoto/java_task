package com.example.task_manager.service;

import com.example.task_manager.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {
    Task createTask(Task task);
    List<Task> findAllByUserId(UUID userId);
    List<Task> findPendingByUserId(UUID userId);
    Optional<Task> findById(UUID id);
    void deleteTask(UUID id);
} 