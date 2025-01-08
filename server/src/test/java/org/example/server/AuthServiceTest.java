package org.example.server;

import org.example.server.config.jwt.JwtTokenProvider;
import org.example.server.dto.user.*;
import org.example.server.entity.Role;
import org.example.server.entity.Utilisateur;
import org.example.server.exception.AppException;
import org.example.server.repository.RoleRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.impl.AuthServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Import(TestSecurityConfig.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private Utilisateur utilisateur;
    private Role role;
    private RegisterDto registerDto;
    private LoginDto loginDto;
    private UtilisateurDtoPost utilisateurDtoPost;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialisation de l'utilisateur
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Jean");
        utilisateur.setPrenom("Dupont");
        utilisateur.setEmail("jean.dupont@example.com");
        utilisateur.setMotDePasse("encodedPassword");
        utilisateur.setAdresse("123 Rue Exemple");
        utilisateur.setTelephone("0123456789");
        utilisateur.setPointsFidelite(100);

        // Initialisation du rôle
        role = new Role();
        role.setName("ROLE_CLIENT");

        // Initialisation du DTO pour l'inscription
        registerDto = new RegisterDto();
        registerDto.setNom("Jean");
        registerDto.setPrenom("Dupont");
        registerDto.setEmail("jean.dupont@example.com");
        registerDto.setMotDePasse("password");
        registerDto.setAdresse("123 Rue Exemple");
        registerDto.setTelephone("0123456789");

        // Initialisation du DTO pour la connexion
        loginDto = new LoginDto();
        loginDto.setEmail("jean.dupont@example.com");
        loginDto.setMotDePasse("password");

        // Initialisation du DTO pour la mise à jour
        utilisateurDtoPost = new UtilisateurDtoPost();
        utilisateurDtoPost.setNom("Jean");
        utilisateurDtoPost.setPrenom("Dupont");
        utilisateurDtoPost.setAdresse("456 Nouvelle Adresse");
        utilisateurDtoPost.setTelephone("0987654321");
    }

    @Test
    public void testRegister_Success() {
        // Arrange
        when(utilisateurRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_CLIENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(registerDto.getMotDePasse())).thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        String result = authService.register(registerDto);

        assertEquals("User Registered Successfully!", result);
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test(expected = AppException.class)
    public void testRegister_EmailAlreadyExists() {
        // Arrange
        when(utilisateurRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        // Act
        authService.register(registerDto);
    }

    @Test
    public void testLogin_Success() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("mockToken");

        utilisateur.setUserRoles(Set.of(role));
        when(auth.getPrincipal()).thenReturn(utilisateur);

        LoginResponse response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test(expected = AuthenticationException.class)
    public void testLogin_Failure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        authService.login(loginDto);
    }

    @Test
    public void testGetIdByEmail_Success() {
        // Arrange
        when(utilisateurRepository.findByEmail("jean.dupont@example.com")).thenReturn(Optional.of(utilisateur));

        // Act
        Long userId = authService.getIdByEmail("jean.dupont@example.com");

        // Assert
        assertEquals(1L, userId.longValue());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testGetIdByEmail_NotFound() {
        // Arrange
        when(utilisateurRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act
        authService.getIdByEmail("unknown@example.com");
    }

    @Test
    public void testUpdateUser_Success() {
        // Arrange
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // Act
        UtilisateurDtoGet result = authService.updateUser(1L, utilisateurDtoPost);

        // Assert
        assertNotNull(result);
        assertEquals("Jean", result.getNom());
        assertEquals("Dupont", result.getPrenom());
        assertEquals("456 Nouvelle Adresse", result.getAdresse());
        assertEquals("0987654321", result.getTelephone());
    }

    @Test
    public void testLogout() {
        // Act
        authService.logout();

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
