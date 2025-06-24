package com.example.task_manager.service;

import com.example.task_manager.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User register(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    List<User> findAll();
} 