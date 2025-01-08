package org.example.server.repository;

import org.example.server.entity.Commande;
import org.example.server.entity.Paiement;
import org.example.server.enums.StatutPaiement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends CrudRepository<Paiement, Long> {
    List<Paiement> findByCommandeId(Long commandeId);
    Optional<Paiement> findById(Long id);
    List<Paiement> findByStatut(StatutPaiement statut);
    @Query("SELECT p FROM paiements p WHERE p.datePaiement BETWEEN :startDate AND :endDate")
    List<Paiement> findByDatePaiementBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.commande FROM paiements p WHERE p.datePaiement BETWEEN :startDate AND :endDate")
    List<Commande> findCommandesByDatePaiement(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

//    @Query(value = "SELECT c.* FROM paiements p JOIN commandes c ON p.commande_id = c.id WHERE p.date_paiement >= :startDate", nativeQuery = true)
//    List<Commande> findCommandesByPaiementDate(@Param("startDate") LocalDateTime startDate);
    @Query("SELECT c FROM commandes c JOIN c.paiement p WHERE p.datePaiement >= :startDate")
    List<Commande> findCommandesByPaiementDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT c FROM commandes c WHERE c.paiement.statut = :statut")
    List<Commande> findCommandesByPaiementStatut(@Param("statut") StatutPaiement statut);




}
