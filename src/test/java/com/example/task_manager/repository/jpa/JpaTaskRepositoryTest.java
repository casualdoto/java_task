package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.Task;
import com.example.task_manager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("jpa")
class JpaTaskRepositoryTest {
    
    @Autowired
    private JpaTaskRepository taskRepository;
    
    @Autowired
    private JpaUserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("testuser", "password"));
    }
    
    @Test
    void save_ShouldSaveAndReturnTask() {
        // Arrange
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        
        // Act
        Task savedTask = taskRepository.save(task);
        
        // Assert
        assertNotNull(savedTask);
        assertNotNull(savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
    }
    
    @Test
    void findById_WhenTaskExists_ShouldReturnTask() {
        // Arrange
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        Task savedTask = taskRepository.save(task);
        
        // Act
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        
        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getTitle());
    }
    
    @Test
    void findByUserIdAndDeletedFalse_ShouldReturnOnlyUserTasksNotDeleted() {
        // Arrange
        Task task1 = new Task("Task 1", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        Task task2 = new Task("Task 2", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        Task deletedTask = new Task("Deleted Task", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        deletedTask.setDeleted(true);
        
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(deletedTask);
        
        // Act
        List<Task> tasks = taskRepository.findByUserIdAndDeletedFalse(testUser.getId());
        
        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().noneMatch(Task::isDeleted));
    }
    
    @Test
    void findAllByDeletedFalse_ShouldReturnAllTasksNotDeleted() {
        // Arrange
        Task task1 = new Task("Task 1", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        Task task2 = new Task("Task 2", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        Task deletedTask = new Task("Deleted Task", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        deletedTask.setDeleted(true);
        
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(deletedTask);
        
        // Act
        List<Task> tasks = taskRepository.findAllByDeletedFalse();
        
        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().noneMatch(Task::isDeleted));
    }
    
    @Test
    void delete_ShouldMarkTaskAsDeleted() {
        // Arrange
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), testUser.getId());
        Task savedTask = taskRepository.save(task);
        
        // Act
        taskRepository.deleteById(savedTask.getId());
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        
        // Assert
        assertTrue(foundTask.isPresent());
        assertTrue(foundTask.get().isDeleted());
    }
} 