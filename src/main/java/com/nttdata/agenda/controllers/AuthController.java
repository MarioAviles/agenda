package com.nttdata.agenda.controllers;

import com.nttdata.agenda.dto.AuthLoginRequest;
import com.nttdata.agenda.dto.AuthRequest;
import com.nttdata.agenda.dto.AuthResponse;
import com.nttdata.agenda.entity.Task;
import com.nttdata.agenda.entity.User;
import com.nttdata.agenda.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public void register(@RequestBody AuthRequest request) {
        authService.register(request);
    }

    @GetMapping
    public List<AuthRequest> getAllUsers() {
        return authService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
    }

}
