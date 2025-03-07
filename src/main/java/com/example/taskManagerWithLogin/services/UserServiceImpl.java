package com.example.taskManagerWithLogin.services;

import com.example.taskManagerWithLogin.exceptions.DuplicateUsernameException;
import com.example.taskManagerWithLogin.exceptions.TaskNotFoundException;
import com.example.taskManagerWithLogin.models.ROLE;
import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.models.dto.TaskDTO;
import com.example.taskManagerWithLogin.models.dto.UserDTO;
import com.example.taskManagerWithLogin.models.dto.UserRegisterDTO;
import com.example.taskManagerWithLogin.repositories.UserRepository;
import com.example.taskManagerWithLogin.repositories.UserRepositoryImpl;
import com.example.taskManagerWithLogin.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepositoryImpl userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> create(User user) {
        if (!EnumSet.allOf(ROLE.class).contains(user.getRole())) {
            return Optional.empty();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepository.create(user);
    }

    @Override
    public Optional<String> register(UserRegisterDTO userRegisterDTO) {

        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(userRegisterDTO.getPassword());
        user.setEmail(userRegisterDTO.getEmail());
        user.setName(userRegisterDTO.getName());
        user.setRole(ROLE.ROLE_USER);

        create(user);

        return Optional.of(jwtUtils.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId()
        ));
    }

    @Override
    public Optional<User> update(Long id, User user) throws DuplicateUsernameException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.update(id, user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<Task> findAllTasksByUser(Long id) {
        return userRepository.findAllTasksByUser(id);
    }

    @Override
    public Optional<Task> findTaskByUserAndTaskId(Long id, Long taskId) {
        return userRepository.findTaskByUserAndTaskId(id, taskId);
    }

    @Override
    public Optional<Task> createTaskByUser(Long id, TaskDTO taskDTO) {

        Task task = mapToTask(taskDTO);
        task.setUserId(id);

        return userRepository.createTaskByUser(id, task);
    }

    public Optional<Task> updateTaskByUser(Long id, Long taskId, Task task) {
        return userRepository.updateTaskByUser(id, taskId, task);
    }

    @Override
    public void deleteTaskByUser(Long id, Long taskId) throws TaskNotFoundException {
        userRepository.deleteTaskByUser(id, taskId);
    }

    @Override
    public List<Task> filterTasksbyStatus(Long id, String status) {
        return userRepository.filterTasksbyStatus(id, status);
    }

    public Optional<Task> updateTaskStatus(Long id, Long taskId, String status) {
        return userRepository.updateTaskStatus(id, taskId, status);
    }

    private Task mapToTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        task.setHasEmailReminder(taskDTO.hasWhatsappReminder());
        return task;
    }
}
