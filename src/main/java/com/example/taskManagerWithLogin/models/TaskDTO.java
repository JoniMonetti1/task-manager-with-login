package com.example.taskManagerWithLogin.models;

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
    private boolean hasWhatsappReminder;

    public boolean hasWhatsappReminder() {
        return hasWhatsappReminder;
    }
}
