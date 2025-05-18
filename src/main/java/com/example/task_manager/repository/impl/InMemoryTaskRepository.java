package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Task save(Task entity) {
        tasks.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void delete(UUID id) {
        Optional.ofNullable(tasks.get(id)).ifPresent(task -> {
            task.setDeleted(true);
            tasks.put(id, task);
        });
    }

    @Override
    public List<Task> findByUserIdAndDeletedFalse(UUID userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId) && !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findAllByDeletedFalse() {
        return tasks.values().stream()
                .filter(task -> !task.isDeleted())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Task> findByUserId(UUID userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Task> findPendingByUserId(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId) 
                        && !task.isDeleted() 
                        && task.getTargetDate().isAfter(now))
                .collect(Collectors.toList());
    }
} 