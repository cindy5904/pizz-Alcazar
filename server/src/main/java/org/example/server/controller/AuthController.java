package org.example.server.controller;

import org.example.server.dto.user.LoginDto;
import org.example.server.dto.user.RegisterDto;
import org.example.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    // Endpoint pour enregistrer un utilisateur
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint pour se connecter (login)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    // Endpoint pour se déconnecter (logout)
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        authService.logout();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }
}
