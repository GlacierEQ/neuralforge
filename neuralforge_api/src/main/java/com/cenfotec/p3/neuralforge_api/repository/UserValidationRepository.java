package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.UserValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserValidationEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations on user validation entities.
 *
 * This repository includes a custom query to retrieve the latest pending validation request for a given email.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Repository
public interface UserValidationRepository extends JpaRepository<UserValidationEntity, String> {

    /**
     * Finds the latest pending validation request associated with a given email.
     *
     * @param email The email of the user whose latest pending validation request is being searched.
     * @return An {@link Optional} containing the latest {@link UserValidationEntity} if found, or empty otherwise.
     */
    @Query("SELECT uv FROM UserValidationEntity uv " +
            "WHERE uv.user.email = :email AND uv.status = false " +
            "ORDER BY uv.requestedAt DESC LIMIT 1")
    Optional<UserValidationEntity> findLatestPendingValidationByEmail(@Param("email") String email);
}
