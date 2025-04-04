package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

/**
 * Resource class representing a learning-focused project.
 * Extends the base {@link ProjectResource} class.
 * 
 * Learning projects are designed for educational purposes and currently
 * inherit all attributes from the parent class. Additional learning-specific
 * attributes can be added here as requirements evolve.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProgrammedGoalProjectResource extends ProjectResource {

  SelectedDaysResource selectedDays;
  @NotNull(message = "Deadline is required")
  @FutureOrPresent(message = "Deadline cannot be in the past")
  Date deadline;
  Boolean notify;
  private List<DynamicContentResource> dynamicContents;
}