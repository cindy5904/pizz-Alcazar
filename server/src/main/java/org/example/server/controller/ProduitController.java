package org.example.server.controller;

import org.example.server.dto.produit.ProduitDtoGet;
import org.example.server.dto.produit.ProduitDtoPost;
import org.example.server.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {
    @Autowired
    private ProduitService produitService;

    // Endpoint pour créer un nouveau produit
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProduitDtoGet> creerProduit(
            @RequestParam("produit") ProduitDtoPost dtoPost, // Assurez-vous que votre front-end envoie correctement ce paramètre
            @RequestParam("image") MultipartFile imageFile) { // Ajoutez ici le paramètre pour le fichier image
        ProduitDtoGet produitCree = produitService.creerProduit(dtoPost, imageFile); // Appel de la méthode avec les deux arguments
        return ResponseEntity.status(HttpStatus.CREATED).body(produitCree);
    }

    // Endpoint pour mettre à jour un produit existant
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{produitId}")
    public ResponseEntity<ProduitDtoGet> mettreAJourProduit(
            @PathVariable Long produitId,
            @RequestParam("produit") ProduitDtoPost dtoPost, // Utilisez également @RequestParam pour la mise à jour si nécessaire
            @RequestParam(value = "image", required = false) MultipartFile imageFile) { // Le paramètre image est optionnel pour la mise à jour
        ProduitDtoGet produitMisAJour = produitService.mettreAJourProduit(produitId, dtoPost, imageFile);
        return ResponseEntity.ok(produitMisAJour);
    }

    // Endpoint pour récupérer un produit par son ID
    @GetMapping("/{produitId}")
    public ResponseEntity<ProduitDtoGet> getProduitById(@PathVariable Long produitId) {
        ProduitDtoGet produit = produitService.getProduitById(produitId);
        return ResponseEntity.ok(produit);
    }

    // Endpoint pour récupérer tous les produits disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<ProduitDtoGet>> getProduitsDisponibles() {
        List<ProduitDtoGet> produits = produitService.getProduitsDisponibles();
        return ResponseEntity.ok(produits);
    }

    // Endpoint pour récupérer les produits par catégorie
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ProduitDtoGet>> getProduitsParCategorie(@PathVariable Long categorieId) {
        List<ProduitDtoGet> produits = produitService.getProduitsParCategorie(categorieId);
        return ResponseEntity.ok(produits);
    }

    // Endpoint pour rechercher des produits par nom
    @GetMapping("/recherche")
    public ResponseEntity<List<ProduitDtoGet>> getProduitsParNom(@RequestParam String nom) {
        List<ProduitDtoGet> produits = produitService.getProduitsParNom(nom);
        return ResponseEntity.ok(produits);
    }

    // Endpoint pour supprimer un produit
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{produitId}")
    public ResponseEntity<Void> supprimerProduit(@PathVariable Long produitId) {
        produitService.supprimerProduit(produitId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
