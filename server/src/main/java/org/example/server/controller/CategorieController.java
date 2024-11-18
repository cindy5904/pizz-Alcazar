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


    @GetMapping
    public ResponseEntity<List<CategorieDtoGet>> getAllCategories() {
        List<CategorieDtoGet> categories = categorieService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDtoGet> getCategorieById(@PathVariable Long id) {
        CategorieDtoGet categorieDto = categorieService.mapToDtoGet(categorieService.getCategorieById(id));
        return ResponseEntity.ok(categorieDto);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategorieDtoGet> createCategorie(@RequestBody CategorieDtoPost dto) {
        CategorieDtoGet createdCategorie = categorieService.createCategorie(dto);
        return ResponseEntity.status(201).body(createdCategorie);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategorieDtoGet> updateCategorie(@PathVariable Long id, @RequestBody CategorieDtoPost dto) {
        CategorieDtoGet updatedCategorie = categorieService.updateCategorie(id, dto);
        return ResponseEntity.ok(updatedCategorie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categorieId}/produits")
    public ResponseEntity<List<Produit>> getProduitsByCategorieId(@PathVariable Long categorieId) {
        List<Produit> produits = categorieService.getProduitsByCategorieId(categorieId);
        return ResponseEntity.ok(produits);
    }
}

