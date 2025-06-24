package com.example.task_manager.model;

import jakarta.validation.constraints.Future;
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
public class Task {
    private UUID id;

    @NotBlank(message = "Заголовок задачи не может быть пустым")
    @Size(min = 3, max = 100, message = "Заголовок должен содержать от 3 до 100 символов")
    private String title;

    @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
    private String description;

    private LocalDateTime creationDate;

    @NotNull(message = "Плановая дата должна быть указана")
    @Future(message = "Плановая дата должна быть в будущем")
    private LocalDateTime targetDate;

    private boolean deleted;

    @NotNull(message = "ID пользователя должен быть указан")
    private UUID userId;

    {
        this.id = UUID.randomUUID();
        this.creationDate = LocalDateTime.now();
        this.deleted = false;
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