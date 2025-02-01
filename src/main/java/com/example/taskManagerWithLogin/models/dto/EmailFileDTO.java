package com.example.taskManagerWithLogin.models.dto;

import org.springframework.web.multipart.MultipartFile;

public record EmailFileDTO(String[] toUser,
                           String subject,
                           String message,
                           MultipartFile file) {
}
