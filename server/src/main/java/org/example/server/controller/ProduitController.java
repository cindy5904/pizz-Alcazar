package org.example.server.controller;

import jakarta.persistence.EntityNotFoundException;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProduitDtoGet> creerProduit(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") double prix,
            @RequestParam("disponibilite") boolean disponibilite,
            @RequestParam(value = "categorieId", required = false) Long categorieId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {


        ProduitDtoPost dtoPost = new ProduitDtoPost();
        dtoPost.setNom(nom);
        dtoPost.setDescription(description);
        dtoPost.setPrix(prix);
        dtoPost.setDisponibilite(disponibilite);
        dtoPost.setCategorieId(categorieId);
        System.out.println("Image reçue : " + (imageFile != null ? imageFile.getOriginalFilename() : "Aucune image")); // Ajoutez ce log

        ProduitDtoGet produitCree = produitService.creerProduit(dtoPost, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(produitCree);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{produitId}")
    public ResponseEntity<ProduitDtoGet> mettreAJourProduit(
            @PathVariable Long produitId,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") double prix,
            @RequestParam("disponibilite") boolean disponibilite,
            @RequestParam(value = "categorieId", required = false) Long categorieId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        // Créez le dtoPost à partir des paramètres du formulaire
        ProduitDtoPost dtoPost = new ProduitDtoPost();
        dtoPost.setNom(nom);
        dtoPost.setDescription(description);
        dtoPost.setPrix(prix);
        dtoPost.setDisponibilite(disponibilite);
        dtoPost.setCategorieId(categorieId);


        ProduitDtoGet produitMisAJour = produitService.mettreAJourProduit(produitId, dtoPost, imageFile);
        return ResponseEntity.ok(produitMisAJour);
    }



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
    public ResponseEntity<?> supprimerProduit(@PathVariable Long produitId) {
        try {
            produitService.supprimerProduit(produitId);
            return ResponseEntity.ok("Produit supprimé avec succès.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible de supprimer le produit.");
        }
    }
}
