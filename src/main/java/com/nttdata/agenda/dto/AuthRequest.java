package com.nttdata.agenda.dto;

import com.nttdata.agenda.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthRequest {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private List<TaskRequest> tasks;
}

