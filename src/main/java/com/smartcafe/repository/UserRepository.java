package com.smartcafe.repository;

import com.smartcafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository - Spring Data JPA
 * 
 * Provides CRUD operations for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (for authentication)
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if username already exists
     */
    boolean existsByUsername(String username);
}
