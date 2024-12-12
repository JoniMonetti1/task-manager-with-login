package com.example.taskManagerWithLogin.repositories;

import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> create(User user);

    Optional<User> update(Long id, User user);

    void delete(Long id);

    List<Task> findAllTasksByUser(Long id);

    Optional<Task> findTaskByUserAndTaskId(Long id, Long taskId);

    Optional<Task> createTaskByUser(Long id, Task task);

    Optional<Task> updateTaskByUser(Long id, Long taskId, Task task);

    void deleteTaskByUser(Long id, Long taskId);
}
