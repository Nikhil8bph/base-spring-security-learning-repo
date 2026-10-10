package com.example.userauthservice.repository;

import com.example.userauthservice.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmailOrMobile(String email, String mobile);

    boolean existsByUsernameOrEmailOrMobile(String username, String email, String mobile);

    boolean existsByEmailOrMobile(String email, String mobile);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByIdAndUsername(Long id, String username);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByMobileAndIdNot(String mobile, Long id);
}
