package org.example.server.repository;

import org.example.server.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    Optional<Role> findRoleBy(Long id);

    boolean existsByName(String name);
}
