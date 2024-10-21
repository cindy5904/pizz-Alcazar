package org.example.server.controller;

import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategorieController {

    @Autowired
    private CategorieService categorieService;

    // Obtenir toutes les catégories
    @GetMapping
    public ResponseEntity<List<Categorie>> getAllCategories() {
        List<Categorie> categories = categorieService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Obtenir une catégorie par ID
    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Long id) {
        Categorie categorie = categorieService.getCategorieById(id);
        return ResponseEntity.ok(categorie);
    }

    // Obtenir une catégorie par nom
    @GetMapping("/nom/{nom}")
    public ResponseEntity<Categorie> getCategorieByNom(@PathVariable String nom) {
        Categorie categorie = categorieService.getCategorieByNom(nom);
        return ResponseEntity.ok(categorie);
    }

    // Vérifier si une catégorie existe par nom
    @GetMapping("/exists/{nom}")
    public ResponseEntity<Boolean> existsByNom(@PathVariable String nom) {
        Boolean exists = categorieService.existsByNom(nom);
        return ResponseEntity.ok(exists);
    }

    // Obtenir les produits d'une catégorie par ID de catégorie
    @GetMapping("/{id}/produits")
    public ResponseEntity<List<Produit>> getProduitsByCategorieId(@PathVariable Long id) {
        List<Produit> produits = categorieService.getProduitsByCategorieId(id);
        return ResponseEntity.ok(produits);
    }

    // Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<Categorie> createCategorie(@RequestBody Categorie categorie) {
        Categorie createdCategorie = categorieService.createCategorie(categorie);
        return new ResponseEntity<>(createdCategorie, HttpStatus.CREATED);
    }

    // Mettre à jour une catégorie
    @PutMapping("/{id}")
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie categorieDetails) {
        Categorie updatedCategorie = categorieService.updateCategorie(id, categorieDetails);
        return ResponseEntity.ok(updatedCategorie);
    }

    // Supprimer une catégorie (optionnel, si nécessaire)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}

