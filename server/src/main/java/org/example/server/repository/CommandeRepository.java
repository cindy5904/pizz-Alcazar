package org.example.server.repository;

import org.example.server.entity.Commande;
import org.example.server.entity.Panier;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.EtatCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends CrudRepository<Commande, Long> {
    List<Commande> findByUser(Utilisateur user);

    List<Commande> findByStatut(EtatCommande statut);

    // Récupérer une commande avec les paiements par son ID
    @Query("SELECT c FROM commandes c JOIN FETCH c.paiement WHERE c.id = :id")
    Optional<Commande> findCommandeWithPaiementById(Long id);


    Optional<Commande> findByPanier(Panier panier);

    Page<Commande> findAll(Pageable pageable);
}
