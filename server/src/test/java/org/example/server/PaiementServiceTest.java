package org.example.server;

import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.Paiement;
import org.example.server.enums.StatutPaiement;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.service.PaiementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class PaiementServiceTest {
    @InjectMocks
    private PaiementService paiementService;

    @Mock
    private PaiementRepository paiementRepository;

    @Mock
    private CommandeRepository commandeRepository;

    private PaiementDtoPost dtoPost;
    private Paiement paiement;

    @Before
    public void setUp() {
        dtoPost = new PaiementDtoPost();
        dtoPost.setMontant(100.0);
        dtoPost.setStatut("REUSSI");
        dtoPost.setMoyenPaiement("CARTE");
        dtoPost.setDatePaiement("2024-10-14T10:15:30");
        dtoPost.setCommandeId(1L);

        paiement = new Paiement();
        paiement.setId(1L);
        paiement.setMontant(100.0);
        paiement.setStatut(StatutPaiement.REUSSI);
        paiement.setMoyenPaiement("CARTE");
        paiement.setDatePaiement(LocalDateTime.now());
    }

    @Test
    public void testCreatePaiement() {
        // Arrange
        when(commandeRepository.findById(dtoPost.getCommandeId())).thenReturn(Optional.of(new Commande())); // Assurez-vous d'avoir une commande à renvoyer
        when(paiementRepository.save(any(Paiement.class))).thenReturn(paiement);

        // Act
        PaiementDtoGet result = paiementService.createPaiement(dtoPost);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        assertEquals(dtoPost.getMontant(), result.getMontant(), 0.01);
        assertEquals(dtoPost.getStatut(), result.getStatut());
    }

    @Test
    public void testGetPaiementById() {
        // Arrange
        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));

        // Act
        PaiementDtoGet result = paiementService.getPaiementById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
    }

    @Test
    public void testGetPaiementsByCommandeId() {
        // Arrange
        List<Paiement> paiements = List.of(paiement);
        when(paiementRepository.findByCommandeId(1L)).thenReturn(paiements);

        // Act
        List<PaiementDtoGet> result = paiementService.getPaiementsByCommandeId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId().longValue());
    }
}
