package com.example.taskManagerWithLogin.services;

import com.example.taskManagerWithLogin.exceptions.TaskNotFoundException;
import com.example.taskManagerWithLogin.models.ROLE;
import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.models.UserDTO;
import com.example.taskManagerWithLogin.repositories.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    UserRepositoryImpl userRepositoryImpl;
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepositoryImpl.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryImpl.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepositoryImpl.findByUsername(username);
    }

    @Override
    public Optional<User> create(User user) {
        if(!EnumSet.allOf(ROLE.class).contains(user.getRol())) {
            return Optional.empty();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepositoryImpl.create(user);
    }

    @Override
    public Optional<User> update(Long id, User user) {
        return userRepositoryImpl.update(id, user);
    }

    @Override
    public void delete(Long id) {
        userRepositoryImpl.delete(id);
    }

    @Override
    public List<Task> findAllTasksByUser(Long id) {
        return userRepositoryImpl.findAllTasksByUser(id);
    }

    @Override
    public Optional<Task> findTaskByUserAndTaskId(Long id, Long taskId) {
        return userRepositoryImpl.findTaskByUserAndTaskId(id, taskId);
    }

    @Override
    public Optional<Task> createTaskByUser(Long id, Task task) {
        return userRepositoryImpl.createTaskByUser(id, task);
    }

    public Optional<Task> updateTaskByUser(Long id, Long taskId, Task task) {
        return userRepositoryImpl.updateTaskByUser(id, taskId, task);
    }

    @Override
    public void deleteTaskByUser(Long id, Long taskId) throws TaskNotFoundException {
        userRepositoryImpl.deleteTaskByUser(id, taskId);
    }

    @Override
    public List<Task> filterTasksbyStatus(Long id, String status) {
        return userRepositoryImpl.filterTasksbyStatus(id, status);
    }
}
