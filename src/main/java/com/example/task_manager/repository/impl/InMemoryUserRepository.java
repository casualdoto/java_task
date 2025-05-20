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

    @Override
    public <S extends User> S save(S entity) {
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(entity -> users.put(entity.getId(), entity));
        return entities;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return users.containsKey(id);
    }

    @Override
    public Iterable<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Iterable<User> findAllById(Iterable<UUID> ids) {
        List<User> result = new ArrayList<>();
        ids.forEach(id -> {
            if (users.containsKey(id)) {
                result.add(users.get(id));
            }
        });
        return result;
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public void deleteById(UUID id) {
        users.remove(id);
    }

    @Override
    public void delete(User entity) {
        users.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        ids.forEach(users::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(entity -> users.remove(entity.getId()));
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
} 