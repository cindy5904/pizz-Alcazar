package org.example.server.repository;

import org.example.server.entity.CommandeItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeItemRepository extends CrudRepository<CommandeItem, Long> {
    Optional<CommandeItem> findById(Long id);

    List<CommandeItem> findByCommandeId(Long commandeId);

    Boolean existsByProduitId(Long produitId);

    Boolean existsByCommandeIdAndProduitId(Long commandeId, Long produitId);
}
