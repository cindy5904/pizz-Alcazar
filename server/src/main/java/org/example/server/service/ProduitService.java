package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.produit.ProduitDtoGet;
import org.example.server.dto.produit.ProduitDtoPost;
import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.example.server.repository.CategorieRepository;
import org.example.server.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduitService {
    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // Mapper de Produit vers ProduitDtoGet
    private ProduitDtoGet mapToDtoGet(Produit produit) {
        ProduitDtoGet dto = new ProduitDtoGet();
        dto.setId(produit.getId());
        dto.setNom(produit.getNom());
        dto.setDescription(produit.getDescription());
        dto.setPrix(produit.getPrix());
        dto.setDisponibilite(produit.isDisponibilite());

        // S'assurer que la catégorie n'est pas nulle avant d'accéder à son nom
        if (produit.getCategorie() != null) {
            dto.setCategorieNom(produit.getCategorie().getNom());
        }

        return dto;
    }

    // Mapper de ProduitDtoPost vers Produit (création)
    private Produit mapToEntity(ProduitDtoPost dtoPost) {
        Produit produit = new Produit();
        produit.setNom(dtoPost.getNom());
        produit.setDescription(dtoPost.getDescription());
        produit.setPrix(dtoPost.getPrix());
        produit.setDisponibilite(dtoPost.isDisponibilite());

        // Associer la catégorie s'il y a un ID de catégorie
        if (dtoPost.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(dtoPost.getCategorieId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));
            produit.setCategorie(categorie);
        }

        return produit;
    }

    // Créer un produit
    public ProduitDtoGet creerProduit(ProduitDtoPost dtoPost) {
        Produit produit = mapToEntity(dtoPost);
        produitRepository.save(produit);
        return mapToDtoGet(produit);
    }

    // Mettre à jour un produit
    public ProduitDtoGet mettreAJourProduit(Long produitId, ProduitDtoPost dtoPost) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

        produit.setNom(dtoPost.getNom());
        produit.setDescription(dtoPost.getDescription());
        produit.setPrix(dtoPost.getPrix());
        produit.setDisponibilite(dtoPost.isDisponibilite());

        // Mettre à jour la catégorie si nécessaire
        if (dtoPost.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(dtoPost.getCategorieId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));
            produit.setCategorie(categorie);
        }

        produitRepository.save(produit);
        return mapToDtoGet(produit);
    }

    // Récupérer un produit par son ID
    public ProduitDtoGet getProduitById(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));
        return mapToDtoGet(produit);
    }

    // Récupérer tous les produits disponibles
    public List<ProduitDtoGet> getProduitsDisponibles() {
        List<Produit> produitsDisponibles = produitRepository.findByDisponibiliteTrue();
        return produitsDisponibles.stream()
                .map(this::mapToDtoGet)
                .collect(Collectors.toList());
    }

    // Récupérer tous les produits d'une catégorie
    public List<ProduitDtoGet> getProduitsParCategorie(Long categorieId) {
        List<Produit> produitsParCategorie = produitRepository.findByCategorieId(categorieId);
        return produitsParCategorie.stream()
                .map(this::mapToDtoGet)
                .collect(Collectors.toList());
    }

    // Récupérer des produits par leur nom
    public List<ProduitDtoGet> getProduitsParNom(String nom) {
        List<Produit> produits = produitRepository.findByNom(nom);
        return produits.stream()
                .map(this::mapToDtoGet)
                .collect(Collectors.toList());
    }

    // Supprimer un produit
    public void supprimerProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));
        produitRepository.delete(produit);
    }
}
