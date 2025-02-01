package com.example.taskManagerWithLogin.services;

import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TaskNotificationScheduler {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(TaskNotificationScheduler.class);

    public TaskNotificationScheduler(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    @Transactional
    public void checkAndSendNotifications() {
        logger.info("Scheduler started at {}", LocalDateTime.now());

        ZoneId buenosAiresZone = ZoneId.of("America/Argentina/Buenos_Aires");
        ZonedDateTime now = ZonedDateTime.now(buenosAiresZone);
        ZonedDateTime notificationTime = now.plusDays(1);

        LocalDateTime start = now.toLocalDateTime();
        LocalDateTime end = notificationTime.toLocalDateTime();

        logger.debug("Searching for tasks between {} and {}", start, end);

        List<Task> tasksDueSoon = userRepository.findTaskByDueDateBetweenAndNotificationSentFalse(start, end);

        logger.debug("Found {} tasks due soon", tasksDueSoon.size());

        for (Task task : tasksDueSoon) {
            try {
                logger.debug("Processing task: ID={}, hasEmailReminder={}, notificationSent={}",
                        task.getTaskId(), task.hasEmailReminder(), task.isNotificationSent());

                if (task.hasEmailReminder() && !task.isNotificationSent()) {
                    Optional<User> user = userRepository.findById(task.getUserId());
                    logger.debug("Found user: {}", user.isPresent() ? user.get().getUsername() : "not found");

                    if (user.isPresent() && user.get().getEmail() != null) {
                        String subject = "Recordatorio: una de tus tareas esta proxima a vencer";
                        String message = "Hola " + user.get().getUsername() + ",\nLa tarea " + task.getName() + " esta proxima a vencer. Por favor revisa tu lista de tareas.";

                        logger.debug("Sending email to {} for task {}", user.get().getEmail(), task.getTaskId());
                        emailService.sendEmail(new String[]{user.get().getEmail()}, subject, message);

                        task.setNotificationSent(true);
                        logger.debug("Updating task {} notification status", task.getTaskId());
                        userRepository.updateTaskByUser(user.get().getId(), task.getTaskId(), task);
                        logger.info("Successfully sent notification for task {} to user {}",
                                task.getTaskId(), user.get().getUsername());
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to process task {}: {}", task.getTaskId(), e.getMessage());
            }
        }
    }
}
