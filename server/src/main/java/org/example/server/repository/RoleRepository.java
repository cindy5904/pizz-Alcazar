package org.example.server.repository;

import org.example.server.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    Role findRoleBy(Long id);
}
