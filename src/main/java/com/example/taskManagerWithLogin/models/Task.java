package com.example.taskManagerWithLogin.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {
    private Long taskId, userId;
    private String name;
    private Status status;
    private LocalDateTime due_date;
}
