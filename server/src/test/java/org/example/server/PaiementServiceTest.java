package org.example.server;

import com.paypal.sdk.PaypalServerSdkClient;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.Paiement;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.StatutPaiement;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.PaiementService;
import org.example.server.service.RecompenseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Import(TestSecurityConfig.class)
public class PaiementServiceTest {

    @Mock
    private PaiementRepository paiementRepository;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RecompenseService recompenseService;

    @Mock
    private PaypalServerSdkClient client;

    @InjectMocks
    private PaiementService paiementService;

    private Commande commande;
    private Utilisateur utilisateur;
    private PaiementDtoPost paiementDtoPost;
    private Paiement paiement;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Jean");
        utilisateur.setPrenom("Dupont");
        utilisateur.setPointsFidelite(50);

        commande = new Commande();
        commande.setId(1L);
        commande.setUser(utilisateur);
        commande.setItemsCommande(new ArrayList<>());

        paiementDtoPost = new PaiementDtoPost();
        paiementDtoPost.setMontant(100.0);
        paiementDtoPost.setStatut(StatutPaiement.valueOf("REUSSI"));
        paiementDtoPost.setMoyenPaiement("CARTE");
        paiementDtoPost.setCommandeId(1L);

        paiement = new Paiement();
        paiement.setId(1L);
        paiement.setMontant(100.0);
        paiement.setStatut(StatutPaiement.REUSSI);
        paiement.setMoyenPaiement("CARTE");
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setCommande(commande);
    }

    @Test
    public void testCreatePaiement_Success() {
        // Arrange
        lenient().when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        lenient().when(paiementRepository.save(any(Paiement.class))).thenReturn(paiement);
        lenient().when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        // Act
        PaiementDtoGet result = paiementService.createPaiement(paiementDtoPost);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, result.getMontant(), 0.01);
        assertEquals("REUSSI", result.getStatut());
        assertEquals("CARTE", result.getMoyenPaiement());

        verify(paiementRepository, times(1)).save(any(Paiement.class));
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    public void testGetPaiementById_Success() {
        // Arrange
        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));

        // Act
        PaiementDtoGet result = paiementService.getPaiementById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        assertEquals(100.0, result.getMontant(), 0.01);
        assertEquals("REUSSI", result.getStatut());

        verify(paiementRepository, times(1)).findById(1L);
    }

    @Test(expected = RuntimeException.class)
    public void testGetPaiementById_NotFound() {
        // Arrange
        when(paiementRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        paiementService.getPaiementById(1L);
    }

    @Test
    public void testGetPaiementsByCommandeId_Success() {
        // Arrange
        List<Paiement> paiements = Arrays.asList(paiement);
        when(paiementRepository.findByCommandeId(1L)).thenReturn(paiements);

        // Act
        List<PaiementDtoGet> result = paiementService.getPaiementsByCommandeId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getMontant(), 0.01);

        verify(paiementRepository, times(1)).findByCommandeId(1L);
    }

    @Test
    public void testValiderDetailsBancaires_ValidDetails() {
        // Act
        boolean isValid = paiementService.validerDetailsBancaires("4111111111111111", "Jean Dupont", "12/25", "123");

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void testValiderDetailsBancaires_InvalidDetails() {
        // Act & Assert
        assertFalse(paiementService.validerDetailsBancaires("1234567890123456", "Jean Dupont", "12/25", "123"));
        assertFalse(paiementService.validerDetailsBancaires("4111111111111111", "Jean Dupont", "12/25", "12"));
        assertFalse(paiementService.validerDetailsBancaires("4111111111111111", "Jean Dupont", "1225", "123"));
    }
}
