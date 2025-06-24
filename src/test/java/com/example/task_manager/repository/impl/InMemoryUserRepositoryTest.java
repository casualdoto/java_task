package com.example.task_manager.repository.impl;

import com.example.task_manager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    
    @Test
    void saveAll_ShouldSaveMultipleUsers() {
        // Arrange
        User anotherUser = new User("anotheruser", "password");
        List<User> usersToSave = Arrays.asList(testUser, anotherUser);
        
        // Act
        Iterable<User> savedUsers = userRepository.saveAll(usersToSave);
        List<User> savedList = convertToList(savedUsers);
        
        // Assert
        assertEquals(2, savedList.size());
        
        // Проверяем, что пользователи действительно сохранены
        Iterable<User> allUsers = userRepository.findAll();
        List<User> allList = convertToList(allUsers);
        assertEquals(2, allList.size());
    }
    
    @Test
    void findAllById_ShouldReturnSpecifiedUsers() {
        // Arrange
        userRepository.save(testUser);
        User user2 = new User("user2", "password");
        userRepository.save(user2);
        User user3 = new User("user3", "password");
        userRepository.save(user3);
        
        List<UUID> idsToFind = Arrays.asList(testUser.getId(), user3.getId());
        
        // Act
        Iterable<User> foundUsers = userRepository.findAllById(idsToFind);
        List<User> foundList = convertToList(foundUsers);
        
        // Assert
        assertEquals(2, foundList.size());
        
        // Проверяем, что найдены правильные пользователи
        Set<String> usernames = foundList.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        assertTrue(usernames.contains("testuser"));
        assertTrue(usernames.contains("user3"));
        assertFalse(usernames.contains("user2"));
    }
    
    @Test
    void count_ShouldReturnNumberOfUsers() {
        // Arrange
        userRepository.save(testUser);
        User user2 = new User("user2", "password");
        userRepository.save(user2);
        
        // Act
        long count = userRepository.count();
        
        // Assert
        assertEquals(2, count);
    }
    
    @Test
    void deleteAllById_ShouldRemoveSpecifiedUsers() {
        // Arrange
        userRepository.save(testUser);
        User user2 = new User("user2", "password");
        userRepository.save(user2);
        User user3 = new User("user3", "password");
        userRepository.save(user3);
        
        List<UUID> idsToDelete = Arrays.asList(testUser.getId(), user3.getId());
        
        // Act
        userRepository.deleteAllById(idsToDelete);
        
        // Assert
        assertFalse(userRepository.existsById(testUser.getId()));
        assertTrue(userRepository.existsById(user2.getId()));
        assertFalse(userRepository.existsById(user3.getId()));
    }
    
    @Test
    void deleteAll_Iterable_ShouldRemoveSpecifiedUsers() {
        // Arrange
        userRepository.save(testUser);
        User user2 = new User("user2", "password");
        userRepository.save(user2);
        User user3 = new User("user3", "password");
        userRepository.save(user3);
        
        List<User> usersToDelete = Arrays.asList(testUser, user3);
        
        // Act
        userRepository.deleteAll(usersToDelete);
        
        // Assert
        assertFalse(userRepository.existsById(testUser.getId()));
        assertTrue(userRepository.existsById(user2.getId()));
        assertFalse(userRepository.existsById(user3.getId()));
    }
    
    @Test
    void deleteAll_ShouldRemoveAllUsers() {
        // Arrange
        userRepository.save(testUser);
        User user2 = new User("user2", "password");
        userRepository.save(user2);
        
        // Act
        userRepository.deleteAll();
        
        // Assert
        assertEquals(0, userRepository.count());
        Iterable<User> remainingUsers = userRepository.findAll();
        List<User> usersList = convertToList(remainingUsers);
        assertTrue(usersList.isEmpty());
    }
    
    @Test
    void existsById_WhenUserExists_ShouldReturnTrue() {
        // Arrange
        userRepository.save(testUser);
        
        // Act
        boolean exists = userRepository.existsById(userId);
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void existsById_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Act
        boolean exists = userRepository.existsById(UUID.randomUUID());
        
        // Assert
        assertFalse(exists);
    }
} 