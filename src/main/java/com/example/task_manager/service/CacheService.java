package com.example.task_manager.service;

import java.util.UUID;

public interface CacheService {
    void evictUserTasksCache(UUID userId);
    void evictAllTasksCache();
    void evictTaskByIdCache(UUID taskId);
    void warmUpCache(UUID userId);
} 