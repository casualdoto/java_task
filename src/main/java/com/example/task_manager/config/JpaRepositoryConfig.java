package com.example.task_manager.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("jpa")
@EnableJpaRepositories(basePackages = "com.example.task_manager.repository.jpa")
@EntityScan("com.example.task_manager.model")
public class JpaRepositoryConfig {
} 