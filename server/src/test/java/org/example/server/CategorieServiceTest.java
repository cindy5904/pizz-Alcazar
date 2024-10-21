package org.example.server;

import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CategorieRepository;
import org.example.server.service.CategorieService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class CategorieServiceTest {
    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreateCategorie_Success() {
        // Arrange
        Categorie categorie = new Categorie();
        categorie.setNom("New Category");

        Mockito.when(categorieRepository.existsByNom(categorie.getNom())).thenReturn(false);
        Mockito.when(categorieRepository.save(categorie)).thenReturn(categorie);

        // Act
        Categorie createdCategorie = categorieService.createCategorie(categorie);

        // Assert
        assertNotNull(createdCategorie);
        assertEquals("New Category", createdCategorie.getNom());
        Mockito.verify(categorieRepository, Mockito.times(1)).save(categorie);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateCategorie_Conflict() {
        // Arrange
        Categorie categorie = new Categorie();
        categorie.setNom("Existing Category");

        Mockito.when(categorieRepository.existsByNom(categorie.getNom())).thenReturn(true);

        // Act
        categorieService.createCategorie(categorie);

        // Assert (exception expected)
    }

    @Test
    public void testGetCategorieById_Success() {
        // Arrange
        Categorie categorie = new Categorie();
        categorie.setId(1L);
        categorie.setNom("Category");

        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));

        // Act
        Categorie foundCategorie = categorieService.getCategorieById(1L);

        // Assert
        assertNotNull(foundCategorie);
        assertEquals("Category", foundCategorie.getNom());
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
        List<Categorie> result = categorieService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateCategorie_Success() {
        // Arrange
        Categorie existingCategorie = new Categorie();
        existingCategorie.setId(1L);
        existingCategorie.setNom("Old Category");

        Categorie updatedCategorie = new Categorie();
        updatedCategorie.setNom("Updated Category");

        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.of(existingCategorie));
        Mockito.when(categorieRepository.save(existingCategorie)).thenReturn(existingCategorie);

        // Act
        Categorie result = categorieService.updateCategorie(1L, updatedCategorie);

        // Assert
        assertEquals("Updated Category", result.getNom());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateCategorie_NotFound() {
        // Arrange
        Categorie updatedCategorie = new Categorie();
        updatedCategorie.setNom("Updated Category");

        Mockito.when(categorieRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        categorieService.updateCategorie(1L, updatedCategorie);

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
        List<Produit> produits = Arrays.asList(new Produit(), new Produit());

        Mockito.when(categorieRepository.findProduitsByCategorieId(1L)).thenReturn(produits);

        // Act
        List<Produit> result = categorieService.getProduitsByCategorieId(1L);

        // Assert
        assertEquals(2, result.size());
    }
}
