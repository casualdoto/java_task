package com.example.task_manager.repository.impl;

import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Profile("inmemory")
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public <S extends Task> S save(S entity) {
        tasks.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Task> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(entity -> tasks.put(entity.getId(), entity));
        return entities;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return tasks.containsKey(id);
    }

    @Override
    public Iterable<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Iterable<Task> findAllById(Iterable<UUID> ids) {
        List<Task> result = new ArrayList<>();
        ids.forEach(id -> {
            if (tasks.containsKey(id)) {
                result.add(tasks.get(id));
            }
        });
        return result;
    }

    @Override
    public long count() {
        return tasks.size();
    }

    @Override
    public void deleteById(UUID id) {
        Optional.ofNullable(tasks.get(id)).ifPresent(task -> {
            task.setDeleted(true);
            tasks.put(id, task);
        });
    }

    @Override
    public void delete(Task entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Task> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        tasks.clear();
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
                .filter(task -> task.getUserId().equals(userId) && !task.isDeleted())
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