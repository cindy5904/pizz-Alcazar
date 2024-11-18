package org.example.server.repository;

import org.example.server.entity.HistoriqueFidelite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueFideliteRepository extends CrudRepository<HistoriqueFidelite, Long> {

    List<HistoriqueFidelite> findByUserId(Long userId);


    Optional<HistoriqueFidelite> findById(Long id);


    List<HistoriqueFidelite> findByDateTransactionBetween(LocalDateTime startDate, LocalDateTime endDate);

}
