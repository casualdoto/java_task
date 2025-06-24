package com.example.task_manager.controller;

import com.example.task_manager.model.Task;
import com.example.task_manager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllUserTasks(@RequestParam UUID userId) {
        List<Task> tasks = taskService.findAllByUserId(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingUserTasks(@RequestParam UUID userId) {
        List<Task> pendingTasks = taskService.findPendingByUserId(userId);
        return new ResponseEntity<>(pendingTasks, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        Optional<Task> task = taskService.findById(id);
        
        if (task.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 