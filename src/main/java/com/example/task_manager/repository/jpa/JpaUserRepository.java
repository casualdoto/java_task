package com.example.task_manager.repository.jpa;

import com.example.task_manager.model.User;
import com.example.task_manager.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile({"prod", "test"})
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {
    Optional<User> findByUsername(String username);
} 