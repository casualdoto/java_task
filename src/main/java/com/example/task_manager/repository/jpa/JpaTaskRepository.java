package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("jpa")
public interface JpaTaskRepository extends JpaRepository<Task, UUID>, TaskRepository {
    List<Task> findByUserIdAndDeletedFalse(UUID userId);
    List<Task> findAllByDeletedFalse();
    
    @Override
    default List<Task> findByUserId(UUID userId) {
        return findByUserIdAndDeletedFalse(userId);
    }
    
    @Override
    default List<Task> findPendingByUserId(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        return findByUserIdAndDeletedFalse(userId).stream()
                .filter(task -> task.getTargetDate().isAfter(now))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    default void deleteById(UUID id) {
        Task task = findById(id).orElse(null);
        if (task != null) {
            task.setDeleted(true);
            save(task);
        }
    }
} 