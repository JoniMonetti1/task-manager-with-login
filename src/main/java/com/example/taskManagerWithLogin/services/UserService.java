package com.example.taskManagerWithLogin.services;


import com.example.taskManagerWithLogin.exceptions.DuplicateUsernameException;
import com.example.taskManagerWithLogin.exceptions.TaskNotFoundException;
import com.example.taskManagerWithLogin.models.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> create(User user);

    Optional<User> register(UserRegisterDTO userRegisterDTO);

    Optional<User> update(Long id, User user) throws DuplicateUsernameException;

    void delete(Long id);

    boolean existsByUsername(String username);

    List<Task> findAllTasksByUser(Long id);

    Optional<Task> findTaskByUserAndTaskId(Long id, Long taskId);

    Optional<Task> createTaskByUser(Long id, TaskDTO taskDTO);

    Optional<Task> updateTaskByUser(Long id, Long taskId, Task task);

    void deleteTaskByUser(Long id, Long taskId) throws TaskNotFoundException;

    List<Task> filterTasksbyStatus(Long id, String status);
}
