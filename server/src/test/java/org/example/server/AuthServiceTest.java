package org.example.server;

import org.example.server.config.jwt.JwtTokenProvider;
import org.example.server.dto.user.LoginDto;
import org.example.server.dto.user.RegisterDto;
import org.example.server.entity.Utilisateur;
import org.example.server.repository.RoleRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.impl.AuthServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Import(TestSecurityConfig.class)
public class AuthServiceTest {
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

    @InjectMocks
    private AuthServiceImpl authService;

    @Before
    public void setUp() {
        // Initialisation avant chaque test, si nécessaire
    }

    @Test
    public void testRegister() {
        // Test de la méthode register
        RegisterDto registerDto = new RegisterDto();
        registerDto.setNom("Dupont");
        registerDto.setPrenom("Jean");
        registerDto.setEmail("jean.dupont@example.com");
        registerDto.setMotDePasse("password");
        registerDto.setAdresse("123 rue de Paris");
        registerDto.setTelephone("0123456789");

        when(utilisateurRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getMotDePasse())).thenReturn("encodedPassword");

        String result = authService.register(registerDto);

        assertEquals("User Registered Successfully!", result);
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    public void testLogin() {
        // Test de la méthode login
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("jean.dupont@example.com");
        loginDto.setMotDePasse("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwtToken");

        String token = authService.login(loginDto);

        assertEquals("jwtToken", token);
    }

    @Test
    public void testGetIdByEmail_UserExists() {
        // Test de getIdByEmail pour utilisateur existant
        String email = "jean.dupont@example.com";
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(utilisateur));

        Long id = authService.getIdByEmail(email);

        assertEquals(Long.valueOf(1), id); // Utilisation de Long.valueOf pour la comparaison
    }


    @Test
    public void testGetIdByEmail_UserNotFound() {
        // Test de getIdByEmail pour utilisateur non trouvé
        String email = "unknown@example.com";
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.getIdByEmail(email);
        });

        assertEquals("User not found with email: unknown@example.com", exception.getMessage());
    }

    @Test
    public void testLoadUserByEmail_UserExists() {
        // Test de loadUserByEmail pour utilisateur existant
        String email = "jean.dupont@example.com";
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(utilisateur));

        UserDetails userDetails = authService.loadUserByEmail(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    public void testLoadUserByEmail_UserNotFound() {
        // Test de loadUserByEmail pour utilisateur non trouvé
        String email = "unknown@example.com";
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByEmail(email);
        });

        assertEquals("User not found with email: unknown@example.com", exception.getMessage());
    }
    @Test
    public void testLogout() {
        // Appel de la méthode de déconnexion
        authService.logout();

        // Vérification que le contexte de sécurité a été effacé
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
