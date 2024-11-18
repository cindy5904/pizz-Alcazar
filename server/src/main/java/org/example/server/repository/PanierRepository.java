package org.example.server.repository;

import org.example.server.entity.Panier;
import org.example.server.entity.PanierItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanierRepository extends CrudRepository<Panier, Long> {
    Optional<Panier> findByUserId(Long userId);
    Optional<Panier> findByUserIdAndActifTrue(Long userId);


}
