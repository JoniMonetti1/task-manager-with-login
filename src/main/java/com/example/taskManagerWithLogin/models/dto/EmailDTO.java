package com.example.taskManagerWithLogin.models.dto;

public record EmailDTO (String[] toUser,
                       String subject,
                       String message){
}
