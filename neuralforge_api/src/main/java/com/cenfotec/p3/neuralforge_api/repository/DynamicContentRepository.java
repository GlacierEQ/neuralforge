package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link DynamicContentEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations.
 * This interface includes custom queries to update content details while ignoring null values.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
@Repository
public interface DynamicContentRepository extends JpaRepository<DynamicContentEntity, String> {

    /**

     *
     * @param email The email.

     */
    List<DynamicContentEntity> findByEmail(String email);

    /**
     * Finds a content entry by its ID.
     *
     * @param id The ID of the content.
     * @return An {@link Optional} containing the content if found, or empty otherwise.
     */
    Optional<DynamicContentEntity> findById(String id);

    /**
     * Updates dynamic content attributes while ignoring null or empty values.
     * Only provided non-null values will be updated in the database.
     *
     * @param id The ID of the content to update.
     * @param title The new title (optional).
     * @param path The new file path (optional).
     * @param type The new type (optional).
     */
    @Transactional
    @Modifying
    @Query("UPDATE DynamicContentEntity d SET " +
            "d.title = CASE WHEN :title IS NOT NULL AND :title <> '' THEN :title ELSE d.title END, " +
            "d.path = CASE WHEN :path IS NOT NULL AND :path <> '' THEN :path ELSE d.path END, " +
            "d.path = CASE WHEN :email IS NOT NULL AND :email <> '' THEN :path ELSE d.email END, " +
            "d.type = CASE WHEN :type IS NOT NULL AND :type <> '' THEN :type ELSE d.type END " +
            "WHERE d.id = :id")
    void updateContentIgnoringNulls(
            @Param("id") String id,
            @Param("title") String title,
            @Param("path") String path,
            @Param("email") String email,
            @Param("type") String type
    );
}