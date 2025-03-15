package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations on the user entity.
 *
 * This interface includes custom queries to update user details while ignoring null values.
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

    /**
     * Updates user attributes while ignoring null or empty values.
     * Only provided non-null values will be updated in the database.
     *
     * @param email The email of the user to update.
     * @param name The new name of the user (optional).
     * @param lastName The new last name of the user (optional).
     * @param password The new password of the user (optional).
     * @param verified The new verification status of the user (optional).
     * @param role The new role ID of the user (optional).
     * @param status The new status of the user (optional).
     */
    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET " +
            "u.name = CASE WHEN :name IS NOT NULL AND :name <> '' THEN :name ELSE u.name END, " +
            "u.lastName = CASE WHEN :lastName IS NOT NULL AND :lastName <> '' THEN :lastName ELSE u.lastName END, " +
            "u.password = CASE WHEN :password IS NOT NULL AND :password <> '' THEN :password ELSE u.password END, " +
            "u.verified = CASE WHEN :verified IS NOT NULL AND :verified = true THEN true ELSE u.verified END, " +
            "u.role.id = CASE WHEN :role IS NOT NULL AND :role <> '' THEN :role ELSE u.role.id END, " +
            "u.status = CASE WHEN :status IS NOT NULL THEN :status ELSE u.status END " +
            "WHERE u.email = :email")
    void updateUserIgnoringNulls(
            @Param("email") String email,
            @Param("name") String name,
            @Param("lastName") String lastName,
            @Param("password") String password,
            @Param("verified") Boolean verified,
            @Param("role") String role,
            @Param("status") Boolean status
    );
}
