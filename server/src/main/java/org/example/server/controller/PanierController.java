package org.example.server.controller;

import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.panier.PanierDtoPost;
import org.example.server.entity.Produit;
import org.example.server.service.PanierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panier")
public class PanierController {
    @Autowired
    private PanierService panierService;

    // Endpoint pour créer un nouveau panier
    @PostMapping
    public ResponseEntity<PanierDtoGet> createPanier(@RequestBody PanierDtoPost dtoPost) {
        PanierDtoGet createdPanier = panierService.createPanier(dtoPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPanier);
    }

    // Endpoint pour obtenir le panier par ID d'utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<PanierDtoGet> getPanierByUserId(@PathVariable Long userId) {
        PanierDtoGet panier = panierService.getPanierByUserId(userId);
        return ResponseEntity.ok(panier);
    }

    // Endpoint pour obtenir les produits d'un panier par ID de panier
    @GetMapping("/{panierId}/produits")
    public ResponseEntity<List<Produit>> getProduitsByPanierId(@PathVariable Long panierId) {
        List<Produit> produits = panierService.getProduitsByPanierId(panierId);
        return ResponseEntity.ok(produits);
    }
}
