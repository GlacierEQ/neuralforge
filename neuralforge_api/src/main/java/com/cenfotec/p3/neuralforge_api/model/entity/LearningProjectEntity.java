package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a learning-focused project in the system.
 * Extends the base {@link ProjectEntity} class and specifies the "LEARNING" discriminator value.
 * 
 * Learning projects are designed for educational purposes, allowing users
 * to practice and develop their skills in a structured environment.
 * Currently, this entity inherits all attributes from the parent class
 * but can be extended with learning-specific attributes as requirements evolve.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "learning_projects")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("LEARNING")
public class LearningProjectEntity extends ProjectEntity {
    
    /**
     * Sets default values before persisting the entity.
     * Ensures the project type is properly set to LEARNING.
     */
    @PrePersist
    public void prePersist() {
        super.setProjectType(ProjectTypeEnum.LEARNING);
    }
}