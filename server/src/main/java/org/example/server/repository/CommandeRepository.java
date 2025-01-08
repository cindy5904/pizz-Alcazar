package org.example.server.repository;

import org.example.server.entity.Commande;
import org.example.server.entity.Panier;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.EtatCommande;
import org.example.server.enums.StatutPaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends CrudRepository<Commande, Long> {
    List<Commande> findByUser(Utilisateur user);

    List<Commande> findByStatut(EtatCommande statut);


    @Query("SELECT c FROM commandes c JOIN FETCH c.paiement WHERE c.id = :id")
    Optional<Commande> findCommandeWithPaiementById(Long id);
    @Query("SELECT c FROM commandes c LEFT JOIN FETCH c.itemsCommande WHERE c.id = :id")
    Optional<Commande> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT c FROM commandes c WHERE c.user.id = :userId")
    Page<Commande> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM commandes c LEFT JOIN c.paiement p WHERE p.datePaiement BETWEEN :startDate AND :endDate")
    List<Commande> findCommandesByDatePaiementBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT c.* " +
            "FROM paiements p " +
            "LEFT JOIN commandes c ON p.commande_id = c.id " +
            "WHERE p.date_paiement BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    List<Commande> findCommandesByPaiementDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT c.* FROM commandes c LEFT JOIN paiements p ON c.paiement_id = p.id WHERE p.date_paiement BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Commande> findCommandesNative(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT c FROM commandes c WHERE c.statut = :commandeStatut AND c.paiement.statut = :paiementStatut")
    List<Commande> findCommandesByCommandeStatutAndPaiementStatut(
            @Param("commandeStatut") EtatCommande commandeStatut,
            @Param("paiementStatut") StatutPaiement paiementStatut
    );

    Optional<Commande> findByPanier(Panier panier);

    Page<Commande> findAll(Pageable pageable);
}
