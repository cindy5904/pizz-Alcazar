package org.example.server.service;

import org.example.server.dto.user.*;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    String register(RegisterDto registerDto);
    LoginResponse login(LoginDto loginDto);



    Long getIdByEmail(String email);

    UserDetails loadUserByEmail(String email);

    void logout();
    UtilisateurDtoGet updateUser(Long userId, UtilisateurDtoPost updatedUser);
}
