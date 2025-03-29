package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Abstract entity representing the base class for all projects in the system.
 * Implements inheritance using joined tables strategy with a discriminator column.
 * 
 * This entity serves as the foundation for specialized project types
 * while maintaining common attributes across all project variants.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "projects")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "project_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ProjectEntity {
    
    /**
     * Unique identifier for the project.
     * Generated automatically as a UUID string.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * ID of the user who created this learning project.
     * This field establishes ownership of the project.
     */
    @Column(nullable = false)
    private String creatorUserId;
    
    /**
     * Name of the project.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * Detailed description of the project.
     * Limited to 1000 characters to allow for comprehensive project outlines
     * while maintaining database performance.
     */
    @Column(length = 1000)
    private String description;
    
    /**
     * Type of project, stored as an enumerated value.
     * This field is managed by the discriminator column mechanism
     * and should not be directly modified.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", insertable = false, updatable = false)
    private ProjectTypeEnum projectType;
}