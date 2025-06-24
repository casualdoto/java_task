package com.example.task_manager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    T save(T entity);
    Optional<T> findById(UUID id);
    List<T> findAll();
    void delete(UUID id);
} 