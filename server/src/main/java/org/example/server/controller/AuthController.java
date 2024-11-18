package org.example.server.controller;

import org.example.server.dto.user.LoginDto;
import org.example.server.dto.user.LoginResponse;
import org.example.server.dto.user.RegisterDto;
import org.example.server.dto.user.UtilisateurDtoGet;
import org.example.server.entity.Role;
import org.example.server.entity.Utilisateur;
import org.example.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginDto loginDto) {
        LoginResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        authService.logout();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @GetMapping("/user/details")
    public ResponseEntity<UtilisateurDtoGet> getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();


        Set<String> roles = utilisateur.getUserRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        UtilisateurDtoGet utilisateurDto = new UtilisateurDtoGet();
        utilisateurDto.setId(utilisateur.getId());
        utilisateurDto.setNom(utilisateur.getNom());
        utilisateurDto.setPrenom(utilisateur.getPrenom());
        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setRoles(roles);

        return ResponseEntity.ok(utilisateurDto);
    }

}
