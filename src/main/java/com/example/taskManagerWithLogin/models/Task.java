package com.example.taskManagerWithLogin.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    private Long taskId;
    @NotBlank(message = "User id is required")
    private Long userId;
    @NotBlank(message = "Name is required")
    private String name;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
}
