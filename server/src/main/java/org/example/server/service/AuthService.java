package org.example.server.service;

import org.example.server.dto.user.LoginDto;
import org.example.server.dto.user.RegisterDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    String register(RegisterDto registerDto);

    String login(LoginDto loginDto);

    Long getIdByEmail(String email);

    UserDetails loadUserByEmail(String email);

    void logout();
}
