package org.example.server.repository;

import org.example.server.entity.Recompense;
import org.example.server.entity.Utilisateur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecompenseRepository extends CrudRepository<Recompense, Long> {
    Optional<Recompense> findByNom(String nom);
    Optional<Recompense> findById(Long id);
    List<Recompense> findByUser(Utilisateur user);
}
