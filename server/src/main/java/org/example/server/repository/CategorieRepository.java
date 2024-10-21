package org.example.server.repository;

import org.example.server.entity.Categorie;
import org.example.server.entity.Produit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends CrudRepository<Categorie, Long> {
    Optional<Categorie> findById(Long id);
    Optional<Categorie> findByNom(String nom);
    Boolean existsByNom(String nom);
    List<Produit> findByProduits_Categorie(Categorie categorie);


}
