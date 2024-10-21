package org.example.server.repository;

import org.example.server.entity.PanierItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanierItemRepository extends CrudRepository<PanierItem, Long> {
    List<PanierItem> findByPanierId(Long panierId);

    // Récupère un item de panier par son produit et son panier
    PanierItem findByProduitIdAndPanierId(Long produitId, Long panierId);
    void deleteById(Long id);

    // Supprime un item de panier par produit et panier
    void deleteByProduitIdAndPanierId(Long produitId, Long panierId);
}
