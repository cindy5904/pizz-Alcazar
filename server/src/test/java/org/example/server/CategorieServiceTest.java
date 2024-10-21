package org.example.server;

import org.example.server.dto.categorie.CategorieDtoGet;
import org.example.server.dto.categorie.CategorieDtoPost;
import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CategorieRepository;
import org.example.server.repository.ProduitRepository;
import org.example.server.service.CategorieService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Import(TestSecurityConfig.class)
public class CategorieServiceTest {
    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreateCategorie_Success() {
        // Arrange
        CategorieDtoPost dtoPost = new CategorieDtoPost();
        dtoPost.setNom("New Category");

        Categorie savedCategorie = new Categorie();
        savedCategorie.setId(1L);
        savedCategorie.setNom("New Category");

        Mockito.when(categorieRepository.existsByNom(dtoPost.getNom())).thenReturn(false);
        Mockito.when(categorieRepository.save(Mockito.any())).thenReturn(savedCategorie);

        // Act
        CategorieDtoGet createdCategorie = categorieService.createCategorie(dtoPost);

        // Assert
        assertNotNull(createdCategorie);
        assertEquals("New Category", createdCategorie.getNom());
        Mockito.verify(categorieRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateCategorie_Conflict() {
        // Arrange
        CategorieDtoPost dtoPost = new CategorieDtoPost();
        dtoPost.setNom("Existing Category");

        // Simuler l'existence d'une catégorie avec ce nom
        Mockito.when(categorieRepository.existsByNom(dtoPost.getNom())).thenReturn(true);

        // Act
        categorieService.createCategorie(dtoPost);


        // Assert (exception expected)
    }

    @Test
    public void testGetCategorieById_Success() {
        // Arrange
        Categorie categorie = new Categorie();
        categorie.setId(1L);
        categorie.setNom("Category");

        // Mocking the repository to return the category when searched by ID
        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));

        // Act
        Categorie foundCategorie = categorieService.getCategorieById(1L); // Appel de la méthode du service

        // Assert
        assertNotNull(foundCategorie); // Vérifiez que l'objet retourné n'est pas null
        assertEquals("Category", foundCategorie.getNom()); // Vérifiez que le nom est correct
        assertEquals(1L, foundCategorie.getId().longValue()); // Vérifiez que l'ID est correct
    }


    @Test(expected = ResourceNotFoundException.class)
    public void testGetCategorieById_NotFound() {
        // Arrange
        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        categorieService.getCategorieById(1L);

        // Assert (exception expected)
    }

    @Test
    public void testGetAllCategories() {
        // Arrange
        List<Categorie> categories = Arrays.asList(new Categorie(), new Categorie());

        Mockito.when(categorieRepository.findAll()).thenReturn(categories);

        // Act
        List<CategorieDtoGet> result = categorieService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateCategorie_Success() {
        // Arrange
        CategorieDtoPost dtoPost = new CategorieDtoPost();
        dtoPost.setNom("Updated Category");

        Categorie existingCategorie = new Categorie();
        existingCategorie.setId(1L);
        existingCategorie.setNom("Old Category");

        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.of(existingCategorie));
        Mockito.when(categorieRepository.save(existingCategorie)).thenReturn(existingCategorie);

        // Act
        CategorieDtoGet updatedCategorie = categorieService.updateCategorie(1L, dtoPost);

        // Assert
        assertEquals("Updated Category", updatedCategorie.getNom());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateCategorie_NotFound() {
        // Arrange
        CategorieDtoPost dtoPost = new CategorieDtoPost();
        dtoPost.setNom("Updated Category");

        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        categorieService.updateCategorie(1L, dtoPost);

        // Assert (exception expected)
    }

    @Test
    public void testDeleteCategorie_Success() {
        // Arrange
        Categorie categorie = new Categorie();
        categorie.setId(1L);

        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        Mockito.doNothing().when(categorieRepository).delete(categorie);

        // Act
        categorieService.deleteCategorie(1L);

        // Assert
        Mockito.verify(categorieRepository, Mockito.times(1)).delete(categorie);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteCategorie_NotFound() {
        // Arrange
        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        categorieService.deleteCategorie(1L);

        // Assert (exception expected)
    }

    @Test
    public void testGetProduitsByCategorie_Success() {
        // Arrange
        Categorie categorie = new Categorie();
        categorie.setId(1L);

        // Créez des produits avec une catégorie associée pour une meilleure vérification
        Produit produit1 = new Produit();
        produit1.setId(1L); // Optionnel : définissez l'ID si nécessaire
        produit1.setCategorie(categorie); // Associer le produit à la catégorie

        Produit produit2 = new Produit();
        produit2.setId(2L); // Optionnel : définissez l'ID si nécessaire
        produit2.setCategorie(categorie); // Associer le produit à la catégorie

        List<Produit> produits = Arrays.asList(produit1, produit2); // Créer la liste des produits

        // Simuler le comportement de categorieRepository
        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));

        // Simuler le comportement de produitRepository
        Mockito.when(produitRepository.findByCategorie(categorie)).thenReturn(produits);

        // Act
        List<Produit> result = categorieService.getProduitsByCategorieId(1L);

        // Assert
        assertNotNull(result); // Vérifiez que le résultat n'est pas nul
        assertEquals(2, result.size()); // Vérifiez le nombre de produits
    }



}
