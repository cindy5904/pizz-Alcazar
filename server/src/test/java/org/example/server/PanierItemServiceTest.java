package org.example.server;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.panierItem.PanierItemDtoGet;
import org.example.server.dto.panierItem.PanierItemDtoPost;
import org.example.server.entity.Panier;
import org.example.server.entity.PanierItem;
import org.example.server.entity.Produit;
import org.example.server.repository.PanierItemRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.ProduitRepository;
import org.example.server.service.PanierItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class PanierItemServiceTest {
    @InjectMocks
    private PanierItemService panierItemService;

    @Mock
    private PanierItemRepository panierItemRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private PanierRepository panierRepository;

    private Panier panier;
    private Produit produit;
    private PanierItem panierItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Initialise les mocks

        // Crée un exemple de Panier, Produit et PanierItem pour les tests
        panier = new Panier();
        panier.setId(1L);

        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Produit Test");

        panierItem = new PanierItem();
        panierItem.setId(1L);
        panierItem.setProduit(produit);
        panierItem.setQuantite(3);
        panierItem.setPanier(panier);
    }

    @Test
    public void testAjouterOuMettreAJourItem_AjouterNouveauItem() {
        // Simule les réponses du repository
        when(panierRepository.findById(anyLong())).thenReturn(Optional.of(panier));
        when(produitRepository.findById(anyLong())).thenReturn(Optional.of(produit));
        when(panierItemRepository.findByProduitIdAndPanierId(anyLong(), anyLong())).thenReturn(null);

        PanierItemDtoPost itemDto = new PanierItemDtoPost();
        itemDto.setProduitId(1L);
        itemDto.setQuantite(2);

        // Appel à la méthode à tester
        PanierItemDtoGet result = panierItemService.ajouterOuMettreAJourItem(itemDto, 1L);

        // Vérifie que les méthodes save et findByProduitIdAndPanierId sont appelées
        verify(panierItemRepository, times(1)).save(any(PanierItem.class));
        assertEquals(2, result.getQuantite());
    }

    @Test
    public void testAjouterOuMettreAJourItem_MettreAJourQuantite() {
        // Simule la situation où l'item existe déjà dans le panier
        when(panierRepository.findById(anyLong())).thenReturn(Optional.of(panier));
        when(produitRepository.findById(anyLong())).thenReturn(Optional.of(produit));
        when(panierItemRepository.findByProduitIdAndPanierId(anyLong(), anyLong())).thenReturn(panierItem);

        PanierItemDtoPost itemDto = new PanierItemDtoPost();
        itemDto.setProduitId(1L);
        itemDto.setQuantite(2);

        // Appel à la méthode à tester
        PanierItemDtoGet result = panierItemService.ajouterOuMettreAJourItem(itemDto, 1L);

        // Vérifie que la quantité est mise à jour correctement
        verify(panierItemRepository, times(1)).save(any(PanierItem.class));
        assertEquals(5, result.getQuantite()); // 3 (existant) + 2 (nouveau)
    }

    @Test
    public void testReduireQuantiteItem_ReduitQuantite() {
        // Simule la situation où l'item existe
        when(panierItemRepository.findByProduitIdAndPanierId(anyLong(), anyLong())).thenReturn(panierItem);

        // Appel à la méthode à tester
        PanierItemDtoGet result = panierItemService.reduireQuantiteItem(1L, 1L, 1);

        // Vérifie que la quantité a été réduite
        verify(panierItemRepository, times(1)).save(any(PanierItem.class));
        assertEquals(2, result.getQuantite()); // 3 (quantité initiale) - 1
    }

    @Test
    public void testReduireQuantiteItem_SupprimeItemSiQuantiteEgaleZero() {
        // Simule la situation où l'item existe
        when(panierItemRepository.findByProduitIdAndPanierId(anyLong(), anyLong())).thenReturn(panierItem);

        // Appel à la méthode à tester avec une réduction qui fait tomber la quantité à 0
        PanierItemDtoGet result = panierItemService.reduireQuantiteItem(1L, 1L, 3);

        // Vérifie que l'item a été supprimé
        verify(panierItemRepository, times(1)).deleteByProduitIdAndPanierId(1L, 1L);
        assertNull(result); // Puisque l'item a été supprimé, le retour doit être null
    }

    @Test
    public void testSupprimerItem() {
        // Simule la situation où l'item existe
        when(panierItemRepository.findByProduitIdAndPanierId(anyLong(), anyLong())).thenReturn(panierItem);

        // Appel à la méthode à tester
        panierItemService.supprimerItem(1L, 1L);

        // Vérifie que l'item a bien été supprimé
        verify(panierItemRepository, times(1)).delete(any(PanierItem.class));
    }

    @Test
    public void testSupprimerItem_ItemNonExistant() {
        // Simule la situation où l'item n'existe pas
        when(panierItemRepository.findByProduitIdAndPanierId(anyLong(), anyLong())).thenReturn(null);

        // Appel à la méthode à tester et vérification de l'exception lancée
        assertThrows(EntityNotFoundException.class, () -> {
            panierItemService.supprimerItem(1L, 1L);
        });
    }
}
