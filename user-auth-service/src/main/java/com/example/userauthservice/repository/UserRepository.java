package com.example.userauthservice.repository;

import com.example.userauthservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmailOrMobile(String email, String mobile);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByMobileAndIdNot(String mobile, Long id);
}
