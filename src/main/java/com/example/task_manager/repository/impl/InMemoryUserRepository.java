package com.example.task_manager.repository.impl;

import com.example.task_manager.model.User;
import com.example.task_manager.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("inmemory")
public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();

    @Override
    public User save(User entity) {
        users.put(entity.getId(), entity);
        usersByUsername.put(entity.getUsername(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(UUID id) {
        Optional.ofNullable(users.get(id)).ifPresent(user -> {
            usersByUsername.remove(user.getUsername());
            users.remove(id);
        });
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
} 