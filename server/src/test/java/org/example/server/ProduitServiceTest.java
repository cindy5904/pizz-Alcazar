package org.example.server;

import org.example.server.dto.produit.ProduitDtoGet;
import org.example.server.dto.produit.ProduitDtoPost;
import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.repository.CategorieRepository;
import org.example.server.repository.ProduitRepository;
import org.example.server.service.ProduitService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class ProduitServiceTest {
    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit;
    private Categorie categorie;
    private ProduitDtoPost produitDtoPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this); // Initialiser les mocks

        // Initialiser les objets de test
        categorie = new Categorie();
        categorie.setId(1L);
        categorie.setNom("Catégorie Test");

        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Produit Test");
        produit.setDescription("Description du produit");
        produit.setPrix(100.0);
        produit.setDisponibilite(true);
        produit.setCategorie(categorie);

        produitDtoPost = new ProduitDtoPost();
        produitDtoPost.setNom("Produit Test");
        produitDtoPost.setDescription("Description du produit");
        produitDtoPost.setPrix(100.0);
        produitDtoPost.setDisponibilite(true);
        produitDtoPost.setCategorieId(1L);
    }

    @Test
    public void testCreerProduit_Succes() {
        // Simuler la catégorie existante dans le repo
        when(categorieRepository.findById(produitDtoPost.getCategorieId())).thenReturn(Optional.of(categorie));

        // Simuler la sauvegarde du produit
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);

        // Appeler la méthode à tester
        ProduitDtoGet produitDtoGet = produitService.creerProduit(produitDtoPost);

        // Vérifier les résultats
        assertNotNull(produitDtoGet);
        assertEquals(produitDtoGet.getNom(), produit.getNom());
        assertEquals(produitDtoGet.getDescription(), produit.getDescription());
        assertEquals(produitDtoGet.getPrix(), produit.getPrix(), 0.0001);
        assertEquals(produitDtoGet.isDisponibilite(), produit.isDisponibilite());

        // Vérifier les interactions avec les dépendances
        verify(categorieRepository, times(1)).findById(produitDtoPost.getCategorieId());
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    public void testMettreAJourProduit_Succes() {
        // Simuler le produit existant et la catégorie
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(produitDtoPost.getCategorieId())).thenReturn(Optional.of(categorie));

        // Simuler la sauvegarde du produit mis à jour
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);

        // Appeler la méthode à tester
        ProduitDtoGet produitDtoGet = produitService.mettreAJourProduit(1L, produitDtoPost);

        // Vérifier les résultats
        assertNotNull(produitDtoGet);
        assertEquals(produitDtoGet.getNom(), produit.getNom());
        assertEquals(produitDtoGet.getDescription(), produit.getDescription());
        assertEquals(produitDtoGet.getPrix(), produit.getPrix(), 0.0001);

        // Vérifier les interactions avec les dépendances
        verify(produitRepository, times(1)).findById(1L);
        verify(categorieRepository, times(1)).findById(produitDtoPost.getCategorieId());
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    public void testGetProduitById_Succes() {
        // Simuler la récupération du produit
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        // Appeler la méthode à tester
        ProduitDtoGet produitDtoGet = produitService.getProduitById(1L);

        // Vérifier les résultats
        assertNotNull(produitDtoGet);
        assertEquals(produitDtoGet.getNom(), produit.getNom());

        // Vérifier les interactions
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetProduitsDisponibles_Succes() {
        // Simuler la récupération des produits disponibles
        List<Produit> produitsDisponibles = new ArrayList<>();
        produitsDisponibles.add(produit);
        when(produitRepository.findByDisponibiliteTrue()).thenReturn(produitsDisponibles);

        // Appeler la méthode à tester
        List<ProduitDtoGet> produitDtoGets = produitService.getProduitsDisponibles();

        // Vérifier les résultats
        assertNotNull(produitDtoGets);
        assertEquals(1, produitDtoGets.size());
        assertEquals(produitDtoGets.get(0).getNom(), produit.getNom());

        // Vérifier les interactions
        verify(produitRepository, times(1)).findByDisponibiliteTrue();
    }

    @Test
    public void testSupprimerProduit_Succes() {
        // Simuler le produit existant
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        // Appeler la méthode à tester
        produitService.supprimerProduit(1L);

        // Vérifier les interactions
        verify(produitRepository, times(1)).findById(1L);
        verify(produitRepository, times(1)).delete(produit);
    }

    @Test
    public void testGetProduitsParCategorie_Succes() {
        // Simuler la récupération des produits par catégorie
        List<Produit> produits = new ArrayList<>();
        produits.add(produit);
        when(produitRepository.findByCategorieId(1L)).thenReturn(produits);

        // Appeler la méthode à tester
        List<ProduitDtoGet> produitDtoGets = produitService.getProduitsParCategorie(1L);

        // Vérifier les résultats
        assertNotNull(produitDtoGets);
        assertEquals(1, produitDtoGets.size());
        assertEquals(produitDtoGets.get(0).getCategorieNom(), produit.getCategorie().getNom());

        // Vérifier les interactions
        verify(produitRepository, times(1)).findByCategorieId(1L);
    }
}
