package com.example.taskManagerWithLogin.models.dto;

import com.example.taskManagerWithLogin.models.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDTO {
    @NotBlank(message = "Name is required")
    private String name;
    private Status status;
    private LocalDateTime dueDate;
    private boolean hasEmailReminder;

    public boolean hasWhatsappReminder() {
        return hasEmailReminder;
    }
}
