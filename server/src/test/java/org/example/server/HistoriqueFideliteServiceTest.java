package org.example.server;

import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoGet;
import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoPost;
import org.example.server.entity.HistoriqueFidelite;
import org.example.server.entity.Recompense;
import org.example.server.entity.Utilisateur;
import org.example.server.repository.HistoriqueFideliteRepository;
import org.example.server.repository.RecompenseRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.HistoriqueFideliteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class HistoriqueFideliteServiceTest {
    @InjectMocks
    private HistoriqueFideliteService historiqueFideliteService;

    @Mock
    private HistoriqueFideliteRepository historiqueFideliteRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RecompenseRepository recompenseRepository;

    private Utilisateur utilisateur;
    private Recompense recompense;
    private HistoriqueFidelite historique;

    @Before
    public void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        recompense = new Recompense();
        recompense.setId(1L);
        recompense.setNom("Bon d'achat");

        historique = new HistoriqueFidelite();
        historique.setId(1L);
        historique.setDateTransaction(LocalDateTime.now());
        historique.setUser(utilisateur);
        historique.setRecompense(recompense); // Si vous avez plusieurs récompenses
    }

    @Test
    public void testCreateHistoriqueFidelite() {
        // Arrange
        HistoriqueFideliteDtoPost dtoPost = new HistoriqueFideliteDtoPost();
        dtoPost.setDateTransaction(LocalDateTime.now().toString());
        dtoPost.setUserId(utilisateur.getId());
        dtoPost.setRecompenseId(recompense.getId());

        when(utilisateurRepository.findById(utilisateur.getId())).thenReturn(Optional.of(utilisateur));
        when(recompenseRepository.findById(recompense.getId())).thenReturn(Optional.of(recompense));
        when(historiqueFideliteRepository.save(any(HistoriqueFidelite.class))).thenReturn(historique);

        // Act
        HistoriqueFideliteDtoGet result = historiqueFideliteService.createHistoriqueFidelite(dtoPost);

        // Assert
        assertNotNull(result);
        assertEquals(Long.valueOf(1), result.getId());
        assertEquals(utilisateur.getId(), result.getUserId());
        assertEquals(recompense.getId(), result.getRecompenseId());
    }

    @Test
    public void testGetHistoriqueParMois() {
        // Arrange
        List<HistoriqueFidelite> historiques = new ArrayList<>();
        historiques.add(historique);

        when(historiqueFideliteRepository.findByDateTransactionBetween(any(), any())).thenReturn(historiques);

        // Act
        List<HistoriqueFideliteDtoGet> result = historiqueFideliteService.getHistoriqueParMois(2024, 10); // Exemple pour octobre 2024

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(historique.getId(), result.get(0).getId());
    }

    @Test
    public void testCountRecompensesParMois() {
        // Arrange
        List<HistoriqueFidelite> historiques = new ArrayList<>();
        historiques.add(historique);

        when(historiqueFideliteRepository.findByDateTransactionBetween(any(), any())).thenReturn(historiques);

        // Act
        long count = historiqueFideliteService.countRecompensesParMois(2024, 10); // Exemple pour octobre 2024

        // Assert
        assertEquals(1, count);
    }
    @Test
    public void testCountRecompensesPourPlusieursUtilisateurs() {
        // Simuler plusieurs utilisateurs
        Utilisateur utilisateur1 = new Utilisateur();
        utilisateur1.setId(1L);
        utilisateur1.setNom("Dupont");
        utilisateur1.setPrenom("Jean");

        Utilisateur utilisateur2 = new Utilisateur();
        utilisateur2.setId(2L);
        utilisateur2.setNom("Martin");
        utilisateur2.setPrenom("Claire");

        // Simuler des récompenses
        Recompense recompense1 = new Recompense();
        recompense1.setId(1L);
        recompense1.setNom("Bon d'achat 10€");

        Recompense recompense2 = new Recompense();
        recompense2.setId(2L);
        recompense2.setNom("Bon d'achat 20€");

        // Ajouter plusieurs historiques de fidélité
        HistoriqueFidelite historique1 = new HistoriqueFidelite();
        historique1.setId(1L);
        historique1.setDateTransaction(LocalDateTime.now());
        historique1.setUser(utilisateur1);
        historique1.setRecompense(recompense1);

        HistoriqueFidelite historique2 = new HistoriqueFidelite();
        historique2.setId(2L);
        historique2.setDateTransaction(LocalDateTime.now());
        historique2.setUser(utilisateur2);
        historique2.setRecompense(recompense2);

        // Mock des dépôts (Utilisez Mockito)
        when(historiqueFideliteRepository.findByDateTransactionBetween(any(), any()))
                .thenReturn(List.of(historique1, historique2));

        // Exécuter la méthode et vérifier le résultat
        long totalRecompenses = historiqueFideliteService.countRecompensesParMois(2024, 10);
        assertEquals(2, totalRecompenses); // Vérifiez que le total est correct
    }

}
