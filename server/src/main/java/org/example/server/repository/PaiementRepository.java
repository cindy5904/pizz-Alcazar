package org.example.server.repository;

import org.example.server.entity.Paiement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends CrudRepository<Paiement, Long> {
    // Recherche tous les paiements associés à une commande
    List<Paiement> findByCommandeId(Long commandeId);

    // Recherche un paiement par ID
    Optional<Paiement> findById(Long id);


}
