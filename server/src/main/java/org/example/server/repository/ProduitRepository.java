package org.example.server.repository;

import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends CrudRepository<Produit, Long> {
    // Récupérer tous les produits disponibles
    List<Produit> findByDisponibiliteTrue();

    // Récupérer tous les produits d'une certaine catégorie
    List<Produit> findByCategorieId(Long categorieId);

    // Récupérer un produit par son nom
    List<Produit> findByNom(String nom);

    // Récupérer un produit par son ID
    Optional<Produit> findById(Long id);
    List<Produit> findByCategorie(Categorie categorie);
}
