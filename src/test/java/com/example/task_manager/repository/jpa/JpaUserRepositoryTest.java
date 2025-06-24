package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("prod")
class JpaUserRepositoryTest {
    
    @Autowired
    private JpaUserRepository userRepository;
    
    @Test
    void save_ShouldSaveAndReturnUser() {
        // Arrange
        User user = new User("testuser", "password");
        
        // Act
        User savedUser = userRepository.save(user);
        
        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
    }
    
    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User user = new User("testuser", "password");
        User savedUser = userRepository.save(user);
        
        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }
    
    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User user = new User("testuser", "password");
        userRepository.save(user);
        
        // Act
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }
    
    @Test
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        User user1 = new User("user1", "password");
        User user2 = new User("user2", "password");
        
        userRepository.save(user1);
        userRepository.save(user2);
        
        // Act
        List<User> users = (List<User>) userRepository.findAll();
        
        // Assert
        assertEquals(2, users.size());
    }
    
    @Test
    void delete_ShouldRemoveUser() {
        // Arrange
        User user = new User("testuser", "password");
        User savedUser = userRepository.save(user);
        
        // Act
        userRepository.deleteById(savedUser.getId());
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        
        // Assert
        assertFalse(deletedUser.isPresent());
    }
} 