package org.example.server.repository;

import org.example.server.entity.PanierItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanierItemRepository extends CrudRepository<PanierItem, Long> {
    List<PanierItem> findByPanierId(Long panierId);
    void deleteByProduitId(Long produitId);

    PanierItem findByProduitIdAndPanierId(Long produitId, Long panierId);
    void deleteById(Long id);

    void deleteByProduitIdAndPanierId(Long produitId, Long panierId);

}
