package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations on the user entity.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user to search for.
     * @return An {@link Optional} containing the user if found, or empty otherwise.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check for existence.
     * @return {@code true} if a user with the email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);
}
