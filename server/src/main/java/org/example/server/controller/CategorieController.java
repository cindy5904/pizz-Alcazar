package org.example.server.controller;

import org.example.server.dto.categorie.CategorieDtoGet;
import org.example.server.dto.categorie.CategorieDtoPost;
import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {
    @Autowired
    private CategorieService categorieService;

    /**
     * Récupérer toutes les catégories.
     * @return la liste des catégories.
     */
    @GetMapping
    public ResponseEntity<List<CategorieDtoGet>> getAllCategories() {
        List<CategorieDtoGet> categories = categorieService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupérer une catégorie par son ID.
     * @param id l'ID de la catégorie.
     * @return la catégorie correspondante.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDtoGet> getCategorieById(@PathVariable Long id) {
        CategorieDtoGet categorieDto = categorieService.mapToDtoGet(categorieService.getCategorieById(id));
        return ResponseEntity.ok(categorieDto);
    }

    /**
     * Créer une nouvelle catégorie.
     * @param dto le DTO de la catégorie à créer.
     * @return la catégorie créée.
     */
    @PostMapping
    public ResponseEntity<CategorieDtoGet> createCategorie(@RequestBody CategorieDtoPost dto) {
        CategorieDtoGet createdCategorie = categorieService.createCategorie(dto);
        return ResponseEntity.status(201).body(createdCategorie);
    }

    /**
     * Mettre à jour une catégorie existante.
     * @param id l'ID de la catégorie à mettre à jour.
     * @param dto le DTO avec les nouvelles informations.
     * @return la catégorie mise à jour.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDtoGet> updateCategorie(@PathVariable Long id, @RequestBody CategorieDtoPost dto) {
        CategorieDtoGet updatedCategorie = categorieService.updateCategorie(id, dto);
        return ResponseEntity.ok(updatedCategorie);
    }

    /**
     * Supprimer une catégorie.
     * @param id l'ID de la catégorie à supprimer.
     * @return une réponse vide.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer tous les produits d'une catégorie par son ID.
     * @param categorieId l'ID de la catégorie.
     * @return la liste des produits de la catégorie.
     */
    @GetMapping("/{categorieId}/produits")
    public ResponseEntity<List<Produit>> getProduitsByCategorieId(@PathVariable Long categorieId) {
        List<Produit> produits = categorieService.getProduitsByCategorieId(categorieId);
        return ResponseEntity.ok(produits);
    }

}

