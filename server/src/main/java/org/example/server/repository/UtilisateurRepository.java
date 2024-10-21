package org.example.server.repository;

import org.example.server.entity.Commande;
import org.example.server.entity.HistoriqueFidelite;
import org.example.server.entity.Role;
import org.example.server.entity.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Utilisateur> findById(Long id);

    @Query("SELECT u.id FROM users u WHERE u.email = :email")
    Long findIdByEmail(@Param("email") String email);

    @Query("SELECT u.commandes FROM users u WHERE u.id = :userId")
    List<Commande> findCommandesByUserId(@Param("userId") Long userId);

    @Query("SELECT u.historiqueFidelite FROM users u WHERE u.id = :userId")
    List<HistoriqueFidelite> findHistoriqueFideliteByUserId(@Param("userId") Long userId);

    @Query("SELECT u.userRoles FROM users u WHERE u.id = :userId")
    Set<Role> findRolesByUserId(@Param("userId") Long userId);
}
