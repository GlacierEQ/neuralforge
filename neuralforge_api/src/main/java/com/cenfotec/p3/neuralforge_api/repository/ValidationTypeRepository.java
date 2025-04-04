package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link ValidationTypeEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations on validation type entities.
 *
 * This repository includes a method to retrieve validation types by their enum value.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Repository
public interface ValidationTypeRepository extends JpaRepository<ValidationTypeEntity, String> {

    /**
     * Finds a validation type entity by its enum value.
     *
     * @param type The {@link ValidationTypeEnum} representing the validation type.
     * @return An {@link Optional} containing the corresponding {@link ValidationTypeEntity} if found, or empty otherwise.
     */
    Optional<ValidationTypeEntity> findByType(ValidationTypeEnum type);
}
