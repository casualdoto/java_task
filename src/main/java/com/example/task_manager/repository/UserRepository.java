package com.example.task_manager.repository;

import com.example.task_manager.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends Repository<User> {
    Optional<User> findByUsername(String username);
} 