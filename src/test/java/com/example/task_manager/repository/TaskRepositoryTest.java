package com.example.task_manager.repository;

import com.example.task_manager.model.Task;
import com.example.task_manager.repository.impl.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    private TaskRepository taskRepository;
    private UUID userId;
    private Task currentTask;
    private Task overdueTask1;
    private Task overdueTask2;
    private Task deletedOverdueTask;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        userId = UUID.randomUUID();
        
        // Текущая задача (не просрочена)
        currentTask = new Task("Current Task", "Description", 
                LocalDateTime.now().plusDays(1), userId);
        
        // Просроченные задачи
        overdueTask1 = new Task("Overdue Task 1", "Description", 
                LocalDateTime.now().minusDays(1), userId);
        overdueTask2 = new Task("Overdue Task 2", "Description", 
                LocalDateTime.now().minusHours(2), userId);
        
        // Удаленная просроченная задача
        deletedOverdueTask = new Task("Deleted Overdue Task", "Description", 
                LocalDateTime.now().minusDays(2), userId);
        deletedOverdueTask.setDeleted(true);
    }

    @Test
    void findOverdueTasks_WhenNoTasks_ShouldReturnEmptyList() {
        // Act
        List<Task> result = taskRepository.findOverdueTasks();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findOverdueTasks_WhenOnlyCurrentTasks_ShouldReturnEmptyList() {
        // Arrange
        taskRepository.save(currentTask);

        // Act
        List<Task> result = taskRepository.findOverdueTasks();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findOverdueTasks_WhenOverdueTasksExist_ShouldReturnOnlyOverdue() {
        // Arrange
        taskRepository.save(currentTask);
        taskRepository.save(overdueTask1);
        taskRepository.save(overdueTask2);

        // Act
        List<Task> result = taskRepository.findOverdueTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(overdueTask1));
        assertTrue(result.contains(overdueTask2));
        assertFalse(result.contains(currentTask));
    }

    @Test
    void findOverdueTasks_ShouldIgnoreDeletedTasks() {
        // Arrange
        taskRepository.save(overdueTask1);
        taskRepository.save(deletedOverdueTask);

        // Act
        List<Task> result = taskRepository.findOverdueTasks();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(overdueTask1));
        assertFalse(result.contains(deletedOverdueTask));
    }

    @Test
    void findOverdueTasks_WithMixedTaskStates_ShouldReturnOnlyNonDeletedOverdue() {
        // Arrange
        taskRepository.save(currentTask);
        taskRepository.save(overdueTask1);
        taskRepository.save(overdueTask2);
        taskRepository.save(deletedOverdueTask);

        // Act
        List<Task> result = taskRepository.findOverdueTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(overdueTask1));
        assertTrue(result.contains(overdueTask2));
        assertFalse(result.contains(currentTask));
        assertFalse(result.contains(deletedOverdueTask));
    }
} 