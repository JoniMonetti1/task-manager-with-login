package com.example.taskManagerWithLogin.controllers;

import com.example.taskManagerWithLogin.models.dto.EmailDTO;
import com.example.taskManagerWithLogin.models.dto.EmailFileDTO;
import com.example.taskManagerWithLogin.services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mail")
public class MailController {

    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sendMessage")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> receiveRequestEmail(@RequestBody EmailDTO emailDTO) {

        System.out.println("Mensaje recibido: " + emailDTO);

        emailService.sendEmail(emailDTO.toUser(), emailDTO.subject(), emailDTO.message());

        Map<String, String> response = new HashMap<>();
        response.put("status", "sent");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendMessageWithFile")
    public ResponseEntity<?> receiveRequestEmailWithFile(@ModelAttribute EmailFileDTO emailFileDTO) {
        try {
            String fileName = emailFileDTO.file().getName();
            Path path = Paths.get("src/main/resources/files/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(emailFileDTO.file().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            File file = path.toFile();

            emailService.sendEmailWithFile(emailFileDTO.toUser(), emailFileDTO.subject(), emailFileDTO.message(), file);

            Map<String, String> response = new HashMap<>();
            response.put("status", "sent");
            response.put("file", fileName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Could not send the email. Error: " + e.getMessage());
        }
    }
}
