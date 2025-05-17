package com.example.task_manager.service;

import com.example.task_manager.model.User;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.repository.impl.InMemoryUserRepository;
import com.example.task_manager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        userService = new UserServiceImpl(userRepository);
        
        userId = UUID.randomUUID();
        testUser = new User("testuser", "password");
        testUser.setId(userId);
    }

    @Test
    void register_ShouldSaveAndReturnUser() {
        // Act
        User result = userService.register(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getPassword(), result.getPassword());
        
        // Проверяем, что пользователь сохранен в репозитории
        Optional<User> savedUser = userRepository.findById(result.getId());
        assertTrue(savedUser.isPresent());
        assertEquals(testUser.getUsername(), savedUser.get().getUsername());
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> result = userService.findByUsername(testUser.getUsername());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        String nonExistentUsername = "nonexistent";

        // Act
        Optional<User> result = userService.findByUsername(nonExistentUsername);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> result = userService.findById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<User> result = userService.findById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        User anotherUser = new User("anotheruser", "password");
        anotherUser.setId(UUID.randomUUID());
        
        userRepository.save(testUser);
        userRepository.save(anotherUser);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals(testUser.getUsername())));
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals(anotherUser.getUsername())));
    }
} 