package com.example.task_manager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface Repository<T> extends CrudRepository<T, UUID> {
} 