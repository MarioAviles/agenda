package com.nttdata.agenda.service;

import com.nttdata.agenda.dto.AuthLoginRequest;
import com.nttdata.agenda.dto.AuthRequest;
import com.nttdata.agenda.dto.AuthResponse;
import com.nttdata.agenda.dto.TaskRequest;
import com.nttdata.agenda.entity.User;
import com.nttdata.agenda.enums.Role;
import com.nttdata.agenda.repository.TaskRepository;
import com.nttdata.agenda.repository.UserRepository;
import com.nttdata.agenda.security.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository; //llamo a donde se almacenan los usuarios
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder; //llamo al codificador de contraseñas
    private final JwtUtil jwtUtil; //llamo a jwt

    public AuthService(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthLoginRequest request) { //le paso por parametro lo que me envian por post
        Optional<User> user = userRepository.findByUsername(request.getUsername()); //cojo el nombre que me han pasado por parametro y uso la funcion para buscarlo
        if (user.isPresent() && passwordEncoder.matches(request.getPassword(),(user.get().getPassword())) ) { //si el usuario existe y la contraseña que me han pasado por parametro coincide con la del usuario
            String token = jwtUtil.generateToken(user.get()); //me genera un token y me lo manda por respuesta
            return new AuthResponse(token);
        }
        throw new RuntimeException("Credenciales incorrectas"); //sino da error
    }

    public void register(AuthRequest request) { //le paso por parametro los datos
        if (userRepository.findByUsername(request.getUsername()).isPresent()) { //busco el nombre para ver si ya existe el usuario
            throw new RuntimeException("El usuario ya existe"); //si existe salta un error
        }
        User user = new User(); //sino crea un nuevo usuario y asigna los datos
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        Role role = (request.getRole() == null || request.getRole().describeConstable().isEmpty()) ? Role.USER : request.getRole();
        user.setRole(role);

        userRepository.save(user);
    }

    public List<AuthRequest> getAllUsers() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return userRepository.findAll().stream().map(user -> new AuthRequest(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), taskRepository.findByUserId(user.getId()).stream().map(task -> new TaskRequest(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getUser().getId()))
                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("No tienes autorización para mostrar los tasks");
        }

    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("task no encontrado"));
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            userRepository.delete(user);
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
