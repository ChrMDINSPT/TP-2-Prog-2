package com.burgerking.backend.repository;

import com.burgerking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByExternalSubject(
            String externalSubject
    );

    boolean existsByExternalSubject(
            String externalSubject
    );
}