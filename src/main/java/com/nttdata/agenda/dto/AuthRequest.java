package com.nttdata.agenda.dto;

import com.nttdata.agenda.enums.Role;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}

