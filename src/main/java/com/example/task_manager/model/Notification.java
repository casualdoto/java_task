package com.example.task_manager.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Notification {
    private UUID id;
    
    @NotBlank(message = "Текст уведомления не может быть пустым")
    @Size(max = 500, message = "Текст уведомления не может быть длиннее 500 символов")
    private String message;
    
    private boolean read;
    private LocalDateTime creationDate;
    
    @NotNull(message = "ID пользователя должен быть указан")
    private UUID userId;

    {
        this.id = UUID.randomUUID();
        this.creationDate = LocalDateTime.now();
        this.read = false;
    }

    public Notification(String message, UUID userId) {
        this.id = UUID.randomUUID();
        this.message = message;
        this.read = false;
        this.creationDate = LocalDateTime.now();
        this.userId = userId;
    }
} 