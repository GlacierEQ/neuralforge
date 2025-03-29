package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Resource class representing a learning-focused project.
 * Extends the base {@link ProjectResource} class.
 * 
 * Learning projects are designed for educational purposes and currently
 * inherit all attributes from the parent class. Additional learning-specific
 * attributes can be added here as requirements evolve.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class LearningProjectResource extends ProjectResource {
  
}