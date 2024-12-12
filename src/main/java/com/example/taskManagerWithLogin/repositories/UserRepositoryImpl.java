package com.example.taskManagerWithLogin.repositories;

import com.example.taskManagerWithLogin.models.Status;
import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository{
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String sql = "SELECT id, username, password, email, name, rol FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("rol")
                )
        );
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, name, rol, password FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{id}, this::mapRowToUser);
        return users.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, email, name, rol, password FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{username}, this::mapRowToUser);
        return users.stream().findFirst();
    }

    public Optional<User> create(User user) {
        String sql = "INSERT INTO users(username, password, email, name, rol) VALUES(?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getName());
            ps.setString(5, user.getRol());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            user.setId(generatedId.longValue());
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public Optional<User> update(Long id, User user) {
        String updateSql = "UPDATE users SET username = ?, password = ?, email = ?, name = ?, rol = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateSql, user.getUsername(), user.getPassword(), user.getEmail(), user.getName(), user.getRol(), id);

        if (rowsAffected > 0) {
            String selectSql = "SELECT id, username, email, name, rol, password FROM users WHERE id = ?";
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
        String sql = "SELECT id_task, id_user, tasks.name, status, tasks.created_at, updated_at, due_date FROM tasks JOIN users ON tasks.id_user = users.id WHERE users.id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                new Task(
                        rs.getLong("id_task"),
                        rs.getLong("id_user"),
                        rs.getString("name"),
                        Status.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime()

                )
        );
    }

    @Override
    public Optional<Task> createTaskByUser(Long id, Task task) {
        return Optional.empty();
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("name"),
                rs.getString("rol")
        );
    }


}
