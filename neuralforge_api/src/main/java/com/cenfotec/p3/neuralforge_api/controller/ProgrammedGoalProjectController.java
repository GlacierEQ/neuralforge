package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import com.cenfotec.p3.neuralforge_api.service.ProgrammedGoalProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller responsible for handling ProgrammedGoal project related requests.
 * Provides endpoints for creating, retrieving, updating, and deleting ProgrammedGoal projects.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@RestController
@RequestMapping("/programmed-goal-projects")
public class ProgrammedGoalProjectController {

    @Autowired
    private ProgrammedGoalProjectService programmedGoalProjectService;

    /**
     * Creates a new programmed goal project.
     *
     * @param projectResource The {@link ProgrammedGoalProjectResource} containing the project details.
     * @return A {@link ResponseEntity} containing the created ProgrammedGoal project.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgrammedGoalProjectResource> createProgrammedGoalProject(@Valid @RequestBody ProgrammedGoalProjectResource projectResource) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(programmedGoalProjectService.createProgrammedGoalProject(projectResource));
    }

    /**
     * Retrieves all ProgrammedGoal projects.
     *
     * @return A {@link ResponseEntity} containing a list of all ProgrammedGoal projects.
     */
    @GetMapping
    public ResponseEntity<List<ProgrammedGoalProjectResource>> getAllProgrammedGoalProjects() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programmedGoalProjectService.getAllProgrammedGoalProjects());
    }

    /**
     * Retrieves all ProgrammedGoal projects created by the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing a list of ProgrammedGoal projects created by the current user.
     */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProgrammedGoalProjectResource>> getCurrentUserProgrammedGoalProjects() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programmedGoalProjectService.getCurrentUserProgrammedGoalProjects());
    }

    /**
     * Retrieves a ProgrammedGoal project by its ID.
     *
     * @param id The ID of the ProgrammedGoal project to retrieve.
     * @return A {@link ResponseEntity} containing the requested ProgrammedGoal project.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgrammedGoalProjectResource> getProgrammedGoalProjectById(@PathVariable String id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programmedGoalProjectService.getProgrammedGoalProjectById(id));
    }

    /**
     * Updates an existing ProgrammedGoal project.
     *
     * @param id The ID of the ProgrammedGoal project to update.
     * @param projectResource The {@link ProgrammedGoalProjectResource} containing updated details.
     * @return A {@link ResponseEntity} containing the updated ProgrammedGoal project.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgrammedGoalProjectResource> updateProgrammedGoalProject(@PathVariable String id, @Valid @RequestBody ProgrammedGoalProjectResource projectResource) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programmedGoalProjectService.updateProgrammedGoalProject(id, projectResource));
    }

    /**
     * Toggles notification settings for a specific ProgrammedGoal project.
     *
     * @param id The ID of the ProgrammedGoal project to toggle notifications for.
     * @return A {@link ResponseEntity} containing the updated ProgrammedGoal project with the new notification setting.
     */
    @PutMapping("/toggle-notifications/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgrammedGoalProjectResource> toggleNotifications(@PathVariable String id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programmedGoalProjectService.toggleNotifications(id));
    }

    /**
     * Deletes a ProgrammedGoal project by its ID.
     *
     * @param id The ID of the ProgrammedGoal project to delete.
     * @return A {@link ResponseEntity} with no content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProgrammedGoalProject(@PathVariable String id) {
        programmedGoalProjectService.deleteProgrammedGoalProject(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}