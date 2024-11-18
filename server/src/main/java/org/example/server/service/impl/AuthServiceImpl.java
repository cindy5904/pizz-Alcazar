package org.example.server.service.impl;

import jakarta.transaction.Transactional;
import org.example.server.config.jwt.JwtTokenProvider;
import org.example.server.dto.user.LoginDto;
import org.example.server.dto.user.LoginResponse;
import org.example.server.dto.user.RegisterDto;
import org.example.server.entity.Role;
import org.example.server.entity.Utilisateur;
import org.example.server.exception.AppException;
import org.example.server.repository.RoleRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Transactional
    @Override
    public String register(RegisterDto registerDto) {
        if (utilisateurRepository.existsByEmail(registerDto.getEmail())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(registerDto.getNom());
        utilisateur.setPrenom(registerDto.getPrenom());
        utilisateur.setEmail(registerDto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(registerDto.getMotDePasse()));
        utilisateur.setAdresse(registerDto.getAdresse());
        utilisateur.setTelephone(registerDto.getTelephone());

        Role roleClient = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
        utilisateur.getUserRoles().add(roleClient);

        utilisateurRepository.save(utilisateur);
        return "User Registered Successfully!";
    }


    @Override
    public LoginResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getMotDePasse())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            // Vérification du type de `principal`
            Object principal = authentication.getPrincipal();
            Utilisateur utilisateur;
            if (principal instanceof Utilisateur) {
                utilisateur = (Utilisateur) principal;
                System.out.println("Utilisateur ID: " + utilisateur.getId()); // Affiche l'ID de l'utilisateur pour vérification
            } else {
                System.out.println("Le principal n'est pas de type Utilisateur");
                throw new IllegalStateException("Type de principal inattendu : " + principal.getClass().getName());
            }

            // Récupération des rôles de l'utilisateur
            Set<String> roles = utilisateur.getUserRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

//            return new LoginResponse(token, roles, utilisateur.getId());
            LoginResponse loginResponse = new LoginResponse(token, roles, utilisateur.getId());
            System.out.println("Contenu de LoginResponse avant retour : " + loginResponse); // Log pour vérifier le contenu
            return loginResponse;
        } catch (InternalAuthenticationServiceException e) {
            // Log l'erreur
            System.out.println("InternalAuthenticationServiceException: " + e.getMessage());
            throw e;
        } catch (AuthenticationException e) {
            // Log l'erreur
            System.out.println("AuthenticationException: " + e.getMessage());
            throw e;
        }
    }



    @Override
    public Long getIdByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(Utilisateur::getId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDetails loadUserByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return loadUserByEmail(email);
    }

    @Override
    public void logout() {
        // Supprime l'authentification de l'utilisateur
        SecurityContextHolder.clearContext();
    }
}
