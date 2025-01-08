package org.example.server.repository;

import org.example.server.entity.Panier;
import org.example.server.entity.PanierItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanierRepository extends CrudRepository<Panier, Long> {
    Optional<Panier> findByUserId(Long userId);
    Optional<Panier> findByUserIdAndActifTrue(Long userId);

    @Modifying
    @Query("UPDATE paniers p SET p.actif = false WHERE p.user.id = :userId AND p.id <> :panierId")
    void deactivateOtherPaniers(@Param("userId") Long userId, @Param("panierId") Long panierId);



}
