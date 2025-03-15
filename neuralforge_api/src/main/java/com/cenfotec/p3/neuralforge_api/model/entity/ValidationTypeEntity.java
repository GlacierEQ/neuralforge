package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing different types of validation within the system.
 * Defines validation categories that can be assigned to various processes.
 *
 * This entity is stored in the "validation_types" table.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Table(name = "validation_types")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationTypeEntity {

    /**
     * Unique identifier for the validation type.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    /**
     * Type of validation, stored as an enumerated value.
     * Ensures that only predefined validation types are used.
     */
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidationTypeEnum type;

    /**
     * Description of the validation type.
     * Provides additional context on what the validation type represents.
     */
    private String description;
}