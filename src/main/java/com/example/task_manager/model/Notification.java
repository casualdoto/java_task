package com.example.task_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 500)
    @NotBlank(message = "Текст уведомления не может быть пустым")
    @Size(max = 500, message = "Текст уведомления не может быть длиннее 500 символов")
    private String message;
    
    @Column(nullable = false)
    private boolean read;
    
    @Column(nullable = false)
    private LocalDateTime creationDate;
    
    @Column(nullable = false)
    @NotNull(message = "ID пользователя должен быть указан")
    private UUID userId;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.creationDate == null) {
            this.creationDate = LocalDateTime.now();
        }
        if (this.read == false) {
            this.read = false;
        }
    }

    public Notification(String message, UUID userId) {
        this.id = UUID.randomUUID();
        this.message = message;
        this.read = false;
        this.creationDate = LocalDateTime.now();
        this.userId = userId;
    }
} 