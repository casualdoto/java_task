package com.example.task_manager.repository.impl;

import com.example.task_manager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;
    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        testUser = new User("testuser", "password");
        userId = testUser.getId();
    }

    @Test
    void save_ShouldSaveAndReturnUser() {
        // Act
        User savedUser = userRepository.save(testUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findById(userId);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findAll_WhenNoUsers_ShouldReturnEmptyList() {
        // Act
        Iterable<User> usersIterable = userRepository.findAll();
        List<User> users = convertToList(usersIterable);

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void findAll_WhenUsersExist_ShouldReturnAllUsers() {
        // Arrange
        userRepository.save(testUser);
        User anotherUser = new User("anotheruser", "password");
        userRepository.save(anotherUser);

        // Act
        Iterable<User> usersIterable = userRepository.findAll();
        List<User> users = convertToList(usersIterable);

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void delete_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        userRepository.save(testUser);

        // Act
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Assert
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void delete_WhenUserDoesNotExist_ShouldDoNothing() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> userRepository.deleteById(nonExistentId));
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findByUsername(testUser.getUsername());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(foundUser.isPresent());
    }
    
    private <T> List<T> convertToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
} 