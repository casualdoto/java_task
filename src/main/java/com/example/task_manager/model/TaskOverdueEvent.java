package com.example.task_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskOverdueEvent {
    private UUID taskId;
    private String title;
    private UUID userId;
    private LocalDateTime targetDate;
    private LocalDateTime overdueDetectedAt;
} 