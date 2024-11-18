package org.example.server.controller;

import org.example.server.dto.panierItem.PanierItemDtoGet;
import org.example.server.dto.panierItem.PanierItemDtoPost;
import org.example.server.service.PanierItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/panier/items")
public class PanierItemController {
    @Autowired
    private PanierItemService panierItemService;


    @PostMapping("/{panierId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<PanierItemDtoGet> ajouterOuMettreAJourItem(
            @PathVariable Long panierId,
            @RequestBody PanierItemDtoPost itemDto) {
        PanierItemDtoGet updatedItem = panierItemService.ajouterOuMettreAJourItem(itemDto, panierId);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedItem);
    }

    @PutMapping("/{panierId}/{produitId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<PanierItemDtoGet> reduireQuantiteItem(
            @PathVariable Long panierId,
            @PathVariable Long produitId,
            @RequestParam int quantite) {
        PanierItemDtoGet updatedItem = panierItemService.reduireQuantiteItem(produitId, panierId, quantite);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{panierId}/{produitId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerItem(
            @PathVariable Long panierId,
            @PathVariable Long produitId) {
        panierItemService.supprimerItem(produitId, panierId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
