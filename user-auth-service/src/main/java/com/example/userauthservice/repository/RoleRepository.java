package com.example.userauthservice.repository;

import com.example.userauthservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByDefaultRoleTrueAndDeletedFalseAndActiveTrue();

    Set<Role> findByDeletedFalseAndActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Role> findByNameIgnoreCase(String role);
}
