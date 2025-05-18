package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("jpa")
public interface JpaTaskRepository extends JpaRepository<Task, UUID>, TaskRepository {
    List<Task> findByUserIdAndDeletedFalse(UUID userId);
    List<Task> findAllByDeletedFalse();
} 