package com.example.task_manager.controller;

import com.example.task_manager.model.Task;
import com.example.task_manager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private Task testTask;
    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testTask = new Task("Test Task", "Test Description", LocalDateTime.now().plusDays(1), userId);
        testTask.setId(taskId);
    }

    @Test
    void getAllUserTasks_ShouldReturnUserTasks() throws Exception {
        // Arrange
        Task anotherTask = new Task("Another Task", "Another Description", LocalDateTime.now().plusDays(2), userId);
        anotherTask.setId(UUID.randomUUID());
        List<Task> tasks = Arrays.asList(testTask, anotherTask);
        when(taskService.findAllByUserId(userId)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()))
                .andExpect(jsonPath("$[1].title").value(anotherTask.getTitle()));
    }

    @Test
    void getPendingUserTasks_ShouldReturnPendingTasks() throws Exception {
        // Arrange
        Task pendingTask = new Task("Pending Task", "Pending Description", LocalDateTime.now().plusDays(3), userId);
        pendingTask.setId(UUID.randomUUID());
        List<Task> pendingTasks = Arrays.asList(testTask, pendingTask);
        when(taskService.findPendingByUserId(userId)).thenReturn(pendingTasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/pending")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()))
                .andExpect(jsonPath("$[1].title").value(pendingTask.getTitle()));
    }

    @Test
    void createTask_ShouldCreateAndReturnTask() throws Exception {
        // Arrange
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(testTask.getTitle()))
                .andExpect(jsonPath("$.description").value(testTask.getDescription()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDeleteAndReturnNoContent() throws Exception {
        // Arrange
        when(taskService.findById(taskId)).thenReturn(Optional.of(testTask));
        doNothing().when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isNoContent());
        
        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(taskService.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isNotFound());
        
        verify(taskService, never()).deleteTask(taskId);
    }
} 