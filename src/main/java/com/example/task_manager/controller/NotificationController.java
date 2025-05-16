package com.example.task_manager.controller;

import com.example.task_manager.model.Notification;
import com.example.task_manager.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllUserNotifications(@RequestParam UUID userId) {
        List<Notification> notifications = notificationService.findAllByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingUserNotifications(@RequestParam UUID userId) {
        List<Notification> pendingNotifications = notificationService.findPendingByUserId(userId);
        return new ResponseEntity<>(pendingNotifications, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 