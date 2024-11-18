package org.example.server;

import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.panier.PanierDtoPost;
import org.example.server.dto.panierItem.PanierItemDtoPost;
import org.example.server.entity.Panier;
import org.example.server.entity.PanierItem;
import org.example.server.entity.Produit;
import org.example.server.entity.Utilisateur;
import org.example.server.repository.PanierItemRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.ProduitRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.PanierService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class PanierserviceTest {
    @InjectMocks
    private PanierService panierService;

    @Mock
    private PanierRepository panierRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PanierItemRepository panierItemRepository;

    @Mock
    private ProduitRepository produitRepository;

    private Utilisateur utilisateur;
    private Panier panier;

    @Before
    public void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        panier = new Panier();
        panier.setId(1L);
        panier.setDateCreation(LocalDateTime.now());
        panier.setDateModification(LocalDateTime.now());
        panier.setUser(utilisateur);
    }

    @Test
    public void testCreatePanier() {
        // Arrange
        PanierDtoPost dtoPost = new PanierDtoPost();
        dtoPost.setUserId(utilisateur.getId());

        // Créez une liste d'itemsPanier pour le DTO
        List<PanierItemDtoPost> itemsPanier = new ArrayList<>();
        PanierItemDtoPost itemDto = new PanierItemDtoPost();
        itemDto.setProduitId(1L); // ID du produit
        itemDto.setQuantite(2); // Quantité
        itemsPanier.add(itemDto);
        dtoPost.setItemsPanier(itemsPanier);

        // Mocking de la recherche de l'utilisateur
        when(utilisateurRepository.findById(utilisateur.getId())).thenReturn(Optional.of(utilisateur));

        // Mocking de la recherche du produit
        Produit produit = new Produit(); // Créez un produit factice
        produit.setId(1L);
        produit.setNom("Produit Test");
        when(produitRepository.findById(itemDto.getProduitId())).thenReturn(Optional.of(produit));

        // Mocking du save du panier
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);
        System.out.println("Avant l'appel à createPanier");
        // Act
        PanierDtoGet result = panierService.addOrUpdatePanierItem(dtoPost);

        System.out.println("Après l'appel à createPanier");
        System.out.println("Valeur retournée : " + result);
        // Assert
        assertNotNull(result);
        assertEquals(panier.getId(), result.getId());


        // Vérifiez l'utilisateur dans le résultat
        assertNotNull(result.getUser());
        assertEquals(utilisateur.getId(), result.getUser().getId());
        assertEquals(utilisateur.getNom(), result.getUser().getNom());
        assertEquals(utilisateur.getPrenom(), result.getUser().getPrenom());

        // Ajoutez d'autres assertions pour vérifier les itemsPanier, etc.
    }



    @Test
    public void testGetPanierByUserId() {
        // Arrange
        when(panierRepository.findByUserId(utilisateur.getId())).thenReturn(Optional.of(panier));

        // Act
        PanierDtoGet result = panierService.getPanierByUserId(utilisateur.getId());

        // Assert
        assertNotNull(result);
        assertEquals(panier.getId(), result.getId());
    }

    @Test
    public void testGetProduitsByPanierId_Success() {
        // Arrange
        Long panierId = 1L;
        Produit produit1 = new Produit();
        produit1.setId(1L);
        Produit produit2 = new Produit();
        produit2.setId(2L);

        PanierItem item1 = new PanierItem();
        item1.setProduit(produit1);
        item1.setPanier(new Panier()); // Juste pour satisfaire la relation

        PanierItem item2 = new PanierItem();
        item2.setProduit(produit2);
        item2.setPanier(new Panier());

        List<PanierItem> items = Arrays.asList(item1, item2);

        Mockito.when(panierItemRepository.findByPanierId(panierId)).thenReturn(items);

        // Act
        List<Produit> result = panierService.getProduitsByPanierId(panierId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(produit1.getId(), result.get(0).getId());
        assertEquals(produit2.getId(), result.get(1).getId());
    }

}
