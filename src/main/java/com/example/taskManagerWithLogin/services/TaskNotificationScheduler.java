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
                        String message =
                                "<html>" +
                                        "<head>" +
                                        "  <style>" +
                                        "    body { font-family: 'Helvetica', Arial, sans-serif; line-height: 1.6; color: #333333; }" +
                                        "    .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eeeeee; border-radius: 5px; }" +
                                        "    .header { background-color: #4A6FFF; color: white; padding: 15px; border-radius: 5px 5px 0 0; text-align: center; }" +
                                        "    .content { padding: 20px; background-color: #ffffff; }" +
                                        "    .task-name { font-weight: bold; color: #4A6FFF; }" +
                                        "    .footer { font-size: 12px; text-align: center; margin-top: 20px; color: #999999; }" +
                                        "    .button { display: inline-block; background-color: #4A6FFF; color: white; padding: 10px 20px; " +
                                        "      text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 15px; }" +
                                        "  </style>" +
                                        "</head>" +
                                        "<body>" +
                                        "  <div class='container'>" +
                                        "    <div class='header'>" +
                                        "      <h2>⏰ Recordatorio de Tarea</h2>" +
                                        "    </div>" +
                                        "    <div class='content'>" +
                                        "      <p>Hola <b>" + user.get().getName() + "</b>,</p>" +
                                        "      <p>Tu tarea <span class='task-name'>" + task.getName() + "</span> está próxima a vencer.</p>" +
                                        "      <p>Por favor revisa tu lista de tareas y actualiza el estado de la misma.</p>" +
                                        "      <center><a href='recuerdito.io' class='button'>Ver mis tareas</a></center>" +
                                        "    </div>" +
                                        "    <div class='footer'>" +
                                        "      <p>Este es un mensaje automático, por favor no respondas a este correo.</p>" +
                                        "      <p>© " + java.time.Year.now().getValue() + " TaskManager. Todos los derechos reservados.</p>" +
                                        "    </div>" +
                                        "  </div>" +
                                        "</body>" +
                                        "</html>";

                        logger.debug("Sending email to {} for task {}", user.get().getEmail(), task.getTaskId());

                        emailService.sendHtmlEmail(new String[]{user.get().getEmail()}, subject, message);

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
