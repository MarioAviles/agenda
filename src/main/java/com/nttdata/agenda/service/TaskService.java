package com.nttdata.agenda.service;

import com.nttdata.agenda.dto.TaskRequest;
import com.nttdata.agenda.entity.Task;
import com.nttdata.agenda.entity.User;
import com.nttdata.agenda.enums.Role;
import com.nttdata.agenda.repository.TaskRepository;
import com.nttdata.agenda.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskRequest> getAllTasks() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findAll().stream().map(task -> new TaskRequest(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getUser().getId()))
                    .collect(Collectors.toList());
        }
        return taskRepository.findByUserId(currentUser.getId()).stream().map(task -> new TaskRequest(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getUser().getId()))
                .collect(Collectors.toList());
    }

    public Task createTask(Task task) {
        User currentUser = getCurrentUser();
        task.setUser(currentUser);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("No existe un task con ese id"));
        User currentUser = getCurrentUser();
        if (existingTask.getUser().getId().equals(currentUser.getId()) || currentUser.getRole() == Role.ADMIN) {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCompleted(updatedTask.isCompleted());
            return taskRepository.save(existingTask);
        }
        throw new RuntimeException("No tienes autorización para actualizar este task");
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("task no encontrado"));
        User currentUser = getCurrentUser();
        if (task.getUser().equals(currentUser.getId()) || currentUser.getRole() == Role.ADMIN) {
            taskRepository.delete(task);
        } else {
            throw new RuntimeException("No tienes autorización para borrar este task");
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
