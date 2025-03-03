package com.example.taskManagerWithLogin.services;

import java.io.File;

public interface EmailService {
    void sendEmail(String[] to, String subject, String message);
    void sendEmailWithFile(String[] toUser, String subject, String message, File file);
    void sendHtmlEmail(String[] toUser, String subject, String message);
}
