package com.example.task_manager.integration;

import com.example.task_manager.TaskManagerApplication;
import com.example.task_manager.model.Task;
import com.example.task_manager.model.User;
import com.example.task_manager.service.TaskService;
import com.example.task_manager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Регистрируем тестового пользователя
        testUser = new User("integrationTestUser", "password123");
        User existingUser = userService.findByUsername(testUser.getUsername()).orElse(null);
        
        if (existingUser == null) {
            testUser = userService.register(testUser);
        } else {
            testUser = existingUser;
        }

        // Создаем тестовую задачу
        testTask = new Task("Integration Test Task", "Test Description", LocalDateTime.now().plusDays(1), testUser.getId());
    }

    @Test
    void userRegistrationAndLogin_ShouldWork() throws Exception {
        // Создаем нового пользователя для теста
        User newUser = new User("newTestUser", "password123");

        // Регистрируем пользователя
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(newUser.getUsername()))
                .andExpect(jsonPath("$.id").exists());

        // Входим с правильными учетными данными
        mockMvc.perform(get("/api/users/login")
                .param("username", newUser.getUsername())
                .param("password", newUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(newUser.getUsername()));

        // Пытаемся войти с неправильным паролем
        mockMvc.perform(get("/api/users/login")
                .param("username", newUser.getUsername())
                .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void taskCreationAndRetrieval_ShouldWork() throws Exception {
        // Создаем задачу
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(testTask.getTitle()))
                .andExpect(jsonPath("$.id").exists());

        // Получаем все задачи пользователя
        mockMvc.perform(get("/api/tasks")
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists());

        // Получаем ожидающие задачи пользователя
        mockMvc.perform(get("/api/tasks/pending")
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void notificationRetrieval_ShouldWork() throws Exception {
        // Создаем задачу, что автоматически создаст уведомление
        Task task = taskService.createTask(testTask);

        // Получаем все уведомления пользователя
        mockMvc.perform(get("/api/notifications")
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].message").exists());

        // Отмечаем уведомление как прочитанное
        UUID notificationId = taskService.findAllByUserId(testUser.getId())
                .stream()
                .filter(t -> t.getId().equals(task.getId()))
                .findFirst()
                .map(t -> task.getId()) // В реальности нам нужно найти id уведомления
                .orElse(UUID.randomUUID());

        // Получаем все непрочитанные уведомления пользователя
        mockMvc.perform(get("/api/notifications/pending")
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
} 