package org.example.server.service.impl;

import jakarta.transaction.Transactional;
import org.example.server.config.jwt.JwtTokenProvider;
import org.example.server.dto.user.LoginDto;
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
    public String login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getMotDePasse())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtTokenProvider.generateToken(authentication);
        } catch (InternalAuthenticationServiceException e) {
            // Log the error
            System.out.println("InternalAuthenticationServiceException: " + e.getMessage());
            throw e;  // Rethrow to let Spring handle it
        } catch (AuthenticationException e) {
            // Log the error
            System.out.println("AuthenticationException: " + e.getMessage());
            throw e;  // Rethrow to let Spring handle it
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
