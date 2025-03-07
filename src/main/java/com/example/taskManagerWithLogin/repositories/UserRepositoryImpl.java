package com.example.taskManagerWithLogin.repositories;

import com.example.taskManagerWithLogin.exceptions.DuplicateUsernameException;
import com.example.taskManagerWithLogin.exceptions.TaskNotFoundException;
import com.example.taskManagerWithLogin.models.ROLE;
import com.example.taskManagerWithLogin.models.Status;
import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.models.dto.UserDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserDTO> findAll() {
        String sql = "SELECT id, username, email, name, role FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new UserDTO(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("name"),
                        ROLE.valueOf(rs.getString("role"))
                )
        );
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, name, role, password, enabled FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{id}, this::mapRowToUser);
        return users.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, email, name, role, password, enabled FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{username}, this::mapRowToUser);
        return users.stream().findFirst();
    }

    public Optional<User> create(User user) {
        String sql = "INSERT INTO users(username, password, email, name, role) VALUES(?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getName());
            ps.setString(5, user.getRole().name());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            user.setId(generatedId.longValue());
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public Optional<User> update(Long id, User user) throws DuplicateUsernameException {
        String checkUsernameSql = "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(checkUsernameSql, Integer.class, user.getUsername(), id);

        if (count > 0) {
            throw new DuplicateUsernameException("Username '" + user.getUsername() + "' already exists");
        }

        String updateSql = "UPDATE users SET username = ?, password = ?, email = ?, name = ?, role = ?, enabled = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateSql, user.getUsername(), user.getPassword(), user.getEmail(), user.getName(), user.getRole().name(), user.isEnabled(), id);

        if (rowsAffected > 0) {
            String selectSql = "SELECT id, username, password, email, name, role, enabled  FROM users WHERE id = ?";
            return jdbcTemplate.query(selectSql, new Object[]{id}, this::mapRowToUser).stream().findFirst();
        }

        return Optional.empty();
    }

    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Task> findAllTasksByUser(Long id) {
        String sql = "SELECT id_task, id_user, tasks.name, status, tasks.created_at, tasks.updated_at, due_date, tasks.has_email_reminder, notification_sent FROM tasks JOIN users ON tasks.id_user = users.id WHERE users.id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                new Task(
                        rs.getLong("id_task"),
                        rs.getLong("id_user"),
                        rs.getString("name"),
                        Status.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime(),
                        rs.getBoolean("has_email_reminder"),
                        rs.getBoolean("notification_sent")

                )
        );
    }

    @Override
    public Optional<Task> findTaskByUserAndTaskId(Long id, Long taskId) {
        String sql = "SELECT id_task, id_user, tasks.name, status, tasks.created_at, tasks.updated_at, due_date, tasks.has_email_reminder, notification_sent FROM tasks JOIN users ON tasks.id_user = users.id WHERE users.id = ? AND tasks.id_task = ?";
        return jdbcTemplate.query(sql, new Object[]{id, taskId}, this::mapRowToTask).stream().findFirst();
    }

    @Override
    public Optional<Task> createTaskByUser(Long id, Task task) {
        Optional<User> user = findById(id);
        if (user.isPresent()) {
            String sql = "INSERT INTO tasks(name, status, due_date, id_user, tasks.has_email_reminder, notification_sent) VALUES(?, ?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, task.getName());
                ps.setString(2, task.getStatus().toString());
                ps.setString(3, task.getDueDate().format(formatter));
                ps.setLong(4, task.getUserId());
                ps.setBoolean(5, task.hasEmailReminder());
                ps.setBoolean(6, task.isNotificationSent());
                return ps;
            }, keyHolder);

            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                task.setTaskId(generatedId.longValue());
                return Optional.of(task);
            }

            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Task> updateTaskByUser(Long id, Long taskId, Task task) {
        Optional<Task> optionalTask = findTaskByUserAndTaskId(id, taskId);
        if (optionalTask.isPresent()) {
            String updateSql = "UPDATE tasks SET name = ?, due_date = ?, status = ?, tasks.has_email_reminder = ?, notification_sent = ? WHERE id_task = ?";
            int rowsAffected = jdbcTemplate.update(updateSql, task.getName(), task.getDueDate(), task.getStatus().name(), task.hasEmailReminder(), task.isNotificationSent(), taskId);

            if (rowsAffected > 0) {
                String selectSql = "SELECT id_task, id_user, tasks.name, tasks.status, tasks.created_at, tasks.updated_at, tasks.due_date, tasks.has_email_reminder, notification_sent FROM tasks WHERE id_task = ?";
                return jdbcTemplate.query(selectSql, new Object[]{taskId}, this::mapRowToTask).stream().findFirst();
            }

            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public void deleteTaskByUser(Long id, Long taskId) throws TaskNotFoundException {
        String sql = "DELETE FROM tasks WHERE id_task = ? AND id_user = ?";
        int rowsAffected = jdbcTemplate.update(sql, taskId, id);
        if (rowsAffected == 0) {
            throw new TaskNotFoundException("Task not found or does not belong to the user");
        }
    }

    @Override
    public List<Task> filterTasksbyStatus(Long id, String status) {
        if (status == null || status.isEmpty()) {
            return findAllTasksByUser(id);
        }

        String sql = "SELECT id_task, id_user, tasks.name, status, tasks.created_at, tasks.updated_at, due_date, tasks.has_email_reminder FROM tasks JOIN users ON tasks.id_user = users.id WHERE users.id = ? AND status = ?";
        return jdbcTemplate.query(sql, new Object[]{id, status}, this::mapRowToTask);
    }

    @Override
    public Optional<Task> updateTaskStatus(Long id, Long taskId, String status) {
        Optional<Task> optionalTask = findTaskByUserAndTaskId(id, taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            Status newStatus = Status.valueOf(status);
            task.setStatus(newStatus);
            return updateTaskByUser(id, taskId, task);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> findTaskByDueDateBetweenAndNotificationSentFalse(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT tasks.id_task, tasks.id_user,tasks.name, tasks.status, tasks.created_at, tasks.updated_at, tasks.due_date, tasks.has_email_reminder, tasks.notification_sent " +
                "FROM tasks " +
                "JOIN users ON tasks.id_user = users.id " +
                "WHERE due_date >= ? AND due_date <= ? " +
                "AND notification_sent = false " +
                "AND has_email_reminder = true";
        return jdbcTemplate.query(sql, new Object[]{start, end}, this::mapRowToTask);
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("name"),
                ROLE.valueOf(rs.getString("role")),
                rs.getBoolean("enabled")
        );
    }

    private Task mapRowToTask(ResultSet rs, int rowNum) throws SQLException {
        return new Task(
                rs.getLong("id_task"),
                rs.getLong("id_user"),
                rs.getString("name"),
                Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getTimestamp("due_date").toLocalDateTime(),
                rs.getBoolean("has_email_reminder"),
                rs.getBoolean("notification_sent")
        );
    }
}
