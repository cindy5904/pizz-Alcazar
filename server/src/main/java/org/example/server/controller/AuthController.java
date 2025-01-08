package org.example.server.controller;

import jakarta.validation.Valid;
import org.example.server.dto.user.*;
import org.example.server.entity.Role;
import org.example.server.entity.Utilisateur;
import org.example.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errors);
        }

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
        utilisateurDto.setAdresse(utilisateur.getAdresse());
        utilisateurDto.setTelephone(utilisateur.getTelephone());
        utilisateurDto.setPointsFidelite(utilisateur.getPointsFidelite());
        utilisateurDto.setRoles(roles);

        return ResponseEntity.ok(utilisateurDto);
    }
    @PutMapping("/update")
    public ResponseEntity<UtilisateurDtoGet> updateUser(@RequestBody UtilisateurDtoPost updatedUser, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();

        System.out.println("Reçu PUT pour utilisateur authentifié ID : " + utilisateur.getId());
        System.out.println("Données à mettre à jour : " + updatedUser);

        UtilisateurDtoGet updatedDto = authService.updateUser(utilisateur.getId(), updatedUser);

        return ResponseEntity.ok(updatedDto);
    }



}
