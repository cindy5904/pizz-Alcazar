package org.example.server;

import org.example.server.dto.recompense.RecompenseDtoGet;
import org.example.server.dto.recompense.RecompenseDtoPost;
import org.example.server.entity.Recompense;
import org.example.server.entity.Utilisateur;
import org.example.server.repository.RecompenseRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.RecompenseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class RecompenseServiceTest {
    @Mock
    private RecompenseRepository recompenseRepository;

    @InjectMocks
    private RecompenseService recompenseService;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    private RecompenseDtoPost recompenseDtoPost;
    private Recompense recompense;

    @Before
    public void setUp() {
        recompenseDtoPost = new RecompenseDtoPost();
        recompenseDtoPost.setNom("Remise 10%");
        recompenseDtoPost.setDescription("Remise de 10% sur la prochaine commande");
        recompenseDtoPost.setPointsNecessaires(100);

        recompense = new Recompense();
        recompense.setId(1L);
        recompense.setNom(recompenseDtoPost.getNom());
        recompense.setDescription(recompenseDtoPost.getDescription());
        recompense.setPointsNecessaires(recompenseDtoPost.getPointsNecessaires());
        recompense.setCodeRemise(UUID.randomUUID().toString().substring(0, 8));
        recompense.setDateRemise(LocalDate.now());
    }

    @Test
    public void createRecompense_ShouldReturnRecompenseDtoGet() {
        when(recompenseRepository.save(any(Recompense.class))).thenReturn(recompense);
        RecompenseDtoGet result = recompenseService.createRecompense(recompenseDtoPost);
        assertNotNull(result);
        assertEquals(recompense.getNom(), result.getNom());
        assertEquals(recompense.getDescription(), result.getDescription());
        assertEquals(recompense.getPointsNecessaires(), result.getPointsNecessaires());
        assertEquals(recompense.getCodeRemise(), result.getCodeRemise());
        assertEquals(recompense.getDateRemise(), result.getDateRemise());
    }

    @Test
    public void genererRecompensePourUtilisateur_ShouldUpdateUserPoints() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPointsFidelite(100);

        // Mock du comportement de l'utilisateur
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        // Appeler la méthode à tester en passant l'ID de l'utilisateur
        recompenseService.genererRecompensePourUtilisateur(1L);

        // Vérifier que les points de fidélité de l'utilisateur ont été réinitialisés
        assertEquals(0, utilisateur.getPointsFidelite());
        // Vérifiez que la récompense a été enregistrée
        verify(recompenseRepository, times(1)).save(any(Recompense.class));
    }


    @Test
    public void getHistoriqueRecompenses_ShouldReturnListOfRecompenseDtoGet() {
        List<Recompense> recompenses = new ArrayList<>();
        recompenses.add(recompense);
        lenient().when(recompenseRepository.findByUtilisateur(any(Utilisateur.class))).thenReturn(recompenses);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(new Utilisateur()));

        List<RecompenseDtoGet> results = recompenseService.getHistoriqueRecompenses(1L);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(recompense.getNom(), results.get(0).getNom());
    }

}
