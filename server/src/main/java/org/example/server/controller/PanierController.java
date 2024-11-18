package org.example.server.controller;

import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.panier.PanierDtoPost;
import org.example.server.dto.panierItem.PanierItemDtoPost;
import org.example.server.entity.Produit;
import org.example.server.service.PanierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panier")
public class PanierController {
    @Autowired
    private PanierService panierService;
    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<PanierDtoGet> addItemToPanier(@RequestParam Long userId, @RequestBody PanierItemDtoPost itemDto) {
        PanierDtoGet updatedPanier = panierService.addOrUpdatePanierItem(userId, itemDto);
        return ResponseEntity.ok(updatedPanier);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<PanierDtoGet> getPanierActifByUserId(@PathVariable Long userId, Authentication authentication) {
        System.out.println("Rôles de l'utilisateur : " + authentication.getAuthorities());
        PanierDtoGet panier = panierService.getPanierActifByUserId(userId);
        return ResponseEntity.ok(panier);
    }

    @GetMapping("/{panierId}/produits")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<Produit>> getProduitsByPanierId(@PathVariable Long panierId) {
        List<Produit> produits = panierService.getProduitsByPanierId(panierId);
        return ResponseEntity.ok(produits);
    }
}
