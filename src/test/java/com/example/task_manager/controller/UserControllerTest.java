package com.example.task_manager.controller;

import com.example.task_manager.model.User;
import com.example.task_manager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User("testuser", "password123");
        testUser.setId(userId);
    }

    @Test
    void register_WhenUserDoesNotExist_ShouldRegisterAndReturnUser() throws Exception {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userService.register(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void register_WhenUserExists_ShouldReturnConflict() throws Exception {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/login")
                .param("username", testUser.getUsername())
                .param("password", testUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void login_WhenUserDoesNotExist_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/login")
                .param("username", testUser.getUsername())
                .param("password", testUser.getPassword()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WhenPasswordIsInvalid_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/login")
                .param("username", testUser.getUsername())
                .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }
} 