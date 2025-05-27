package com.example.task_manager.service.impl;

import com.example.task_manager.service.CacheService;
import com.example.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {
    
    private final CacheManager cacheManager;
    private final TaskService taskService;
    
    @Override
    public void evictUserTasksCache(UUID userId) {
        log.debug("Очистка кэша задач для пользователя: {}", userId);
        
        var userTasksCache = cacheManager.getCache("userTasks");
        var pendingTasksCache = cacheManager.getCache("pendingTasks");
        
        if (userTasksCache != null) {
            userTasksCache.evict(userId);
        }
        if (pendingTasksCache != null) {
            pendingTasksCache.evict(userId);
        }
    }
    
    @Override
    public void evictAllTasksCache() {
        log.debug("Очистка всех кэшей задач");
        
        var userTasksCache = cacheManager.getCache("userTasks");
        var pendingTasksCache = cacheManager.getCache("pendingTasks");
        var taskByIdCache = cacheManager.getCache("taskById");
        
        if (userTasksCache != null) {
            userTasksCache.clear();
        }
        if (pendingTasksCache != null) {
            pendingTasksCache.clear();
        }
        if (taskByIdCache != null) {
            taskByIdCache.clear();
        }
    }
    
    @Override
    public void evictTaskByIdCache(UUID taskId) {
        log.debug("Очистка кэша задачи: {}", taskId);
        
        var taskByIdCache = cacheManager.getCache("taskById");
        if (taskByIdCache != null) {
            taskByIdCache.evict(taskId);
        }
    }
    
    @Override
    public void warmUpCache(UUID userId) {
        log.debug("Прогрев кэша для пользователя: {}", userId);
        
        // Предварительно загружаем данные в кэш
        taskService.findAllByUserId(userId);
        taskService.findPendingByUserId(userId);
    }
} 