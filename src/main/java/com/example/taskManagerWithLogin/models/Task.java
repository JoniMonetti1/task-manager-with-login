package com.example.taskManagerWithLogin.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    private Long taskId;
    private Long userId;
    @NotBlank(message = "Name is required")
    private String name;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    @Setter
    private boolean hasEmailReminder;
    private boolean notificationSent;

    public boolean hasEmailReminder() {
        return hasEmailReminder;
    }
}
