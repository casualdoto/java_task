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

import java.util.List;
import java.util.UUID;

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
        return findByUserIdAndDeletedFalse(userId);
    }
    
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.deleted = true WHERE t.id = :id")
    void softDeleteById(@Param("id") UUID id);
    
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