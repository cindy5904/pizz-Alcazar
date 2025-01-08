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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
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
    private Produit produit;

    @Before
    public void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Produit Test");

        panier = new Panier();
        panier.setId(1L);
        panier.setDateCreation(LocalDateTime.now());
        panier.setDateModification(LocalDateTime.now());
        panier.setUser(utilisateur);
        panier.setActif(true);
        panier.setItemsPanier(new ArrayList<>());
    }

    // ======= Tests Existant =======

    @Test
    public void testCreatePanier() {
        PanierDtoPost dtoPost = new PanierDtoPost();
        dtoPost.setUserId(utilisateur.getId());

        List<PanierItemDtoPost> itemsPanier = new ArrayList<>();
        PanierItemDtoPost itemDto = new PanierItemDtoPost();
        itemDto.setProduitId(1L);
        itemDto.setQuantite(2);
        itemsPanier.add(itemDto);
        dtoPost.setItemsPanier(itemsPanier);

        when(utilisateurRepository.findById(utilisateur.getId())).thenReturn(Optional.of(utilisateur));
        when(produitRepository.findById(itemDto.getProduitId())).thenReturn(Optional.of(produit));
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);

        PanierDtoGet result = panierService.addOrUpdatePanierItem(utilisateur.getId(), itemDto);

        assertNotNull(result);
        assertEquals(panier.getId(), result.getId());
        assertNotNull(result.getUser());
        assertEquals(utilisateur.getId(), result.getUser().getId());
        assertEquals(utilisateur.getNom(), result.getUser().getNom());
        assertEquals(utilisateur.getPrenom(), result.getUser().getPrenom());
    }

    @Test
    public void testGetPanierByUserId() {
        when(panierRepository.findByUserId(utilisateur.getId())).thenReturn(Optional.of(panier));

        PanierDtoGet result = panierService.getPanierByUserId(utilisateur.getId());

        assertNotNull(result);
        assertEquals(panier.getId(), result.getId());
    }

    @Test
    public void testGetProduitsByPanierId_Success() {
        Long panierId = 1L;
        Produit produit1 = new Produit();
        produit1.setId(1L);
        Produit produit2 = new Produit();
        produit2.setId(2L);

        PanierItem item1 = new PanierItem();
        item1.setProduit(produit1);
        item1.setPanier(panier);

        PanierItem item2 = new PanierItem();
        item2.setProduit(produit2);
        item2.setPanier(panier);

        List<PanierItem> items = Arrays.asList(item1, item2);

        when(panierItemRepository.findByPanierId(panierId)).thenReturn(items);

        List<Produit> result = panierService.getProduitsByPanierId(panierId);

        assertEquals(2, result.size());
        assertEquals(produit1.getId(), result.get(0).getId());
        assertEquals(produit2.getId(), result.get(1).getId());
    }

    // ======= Nouveaux Tests =======

    @Test
    public void testAddNewPanierItem() {
        PanierItemDtoPost itemDto = new PanierItemDtoPost();
        itemDto.setProduitId(1L);
        itemDto.setQuantite(2);

        when(utilisateurRepository.findById(utilisateur.getId())).thenReturn(Optional.of(utilisateur));
        lenient().when(panierRepository.findByUserIdAndActifTrue(utilisateur.getId())).thenReturn(Optional.of(panier));
        when(produitRepository.findById(produit.getId())).thenReturn(Optional.of(produit));
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);

        PanierDtoGet result = panierService.addOrUpdatePanierItem(utilisateur.getId(), itemDto);

        assertNotNull(result);
        assertEquals(1, result.getItemsPanier().size());
        assertEquals(produit.getId(), result.getItemsPanier().get(0).getProduit().getId());
        assertEquals(2, result.getItemsPanier().get(0).getQuantite());
    }

    @Test
    public void testUpdateExistingPanierItem() {
        PanierItem existingItem = new PanierItem();
        existingItem.setId(1L);
        existingItem.setProduit(produit);
        existingItem.setQuantite(1);
        existingItem.setPanier(panier);
        panier.setItemsPanier(Collections.singletonList(existingItem));

        PanierItemDtoPost itemDto = new PanierItemDtoPost();
        itemDto.setProduitId(1L);
        itemDto.setQuantite(3);

        when(panierRepository.findByUserIdAndActifTrue(utilisateur.getId())).thenReturn(Optional.of(panier));
        when(panierItemRepository.save(any(PanierItem.class))).thenReturn(existingItem);
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);

        PanierDtoGet result = panierService.addOrUpdatePanierItem(utilisateur.getId(), itemDto);

        assertNotNull(result);
        assertEquals(1, result.getItemsPanier().size());
        assertEquals(4, result.getItemsPanier().get(0).getQuantite());
    }
}

