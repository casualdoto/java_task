package com.example.task_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Заголовок задачи не может быть пустым")
    @Size(min = 3, max = 100, message = "Заголовок должен содержать от 3 до 100 символов")
    private String title;

    @Column(length = 1000)
    @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
    private String description;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    @NotNull(message = "Плановая дата должна быть указана")
    @Future(message = "Плановая дата должна быть в будущем")
    private LocalDateTime targetDate;

    @Column(nullable = false)
    private boolean deleted;

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
        if (this.deleted == false) {
            this.deleted = false;
        }
    }

    public Task(String title, String description, LocalDateTime targetDate, UUID userId) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.targetDate = targetDate;
        this.deleted = false;
        this.userId = userId;
    }
} 