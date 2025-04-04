package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.LearningProjectResource;
import com.cenfotec.p3.neuralforge_api.service.LearningProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling learning project related requests.
 * Provides endpoints for creating, retrieving, updating, and deleting learning projects.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@RestController
@RequestMapping("/learning-projects")
public class LearningProjectController {

    @Autowired
    private LearningProjectService learningProjectService;

    /**
     * Creates a new learning project.
     *
     * @param projectResource The {@link LearningProjectResource} containing the project details.
     * @return A {@link ResponseEntity} containing the created learning project.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LearningProjectResource> createLearningProject(@Valid @RequestBody LearningProjectResource projectResource) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(learningProjectService.createLearningProject(projectResource));
    }

    /**
     * Retrieves all learning projects.
     *
     * @return A {@link ResponseEntity} containing a list of all learning projects.
     */
    @GetMapping
    public ResponseEntity<List<LearningProjectResource>> getAllLearningProjects() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(learningProjectService.getAllLearningProjects());
    }

    /**
     * Retrieves all learning projects created by the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing a list of learning projects created by the current user.
     */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LearningProjectResource>> getCurrentUserLearningProjects() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(learningProjectService.getCurrentUserLearningProjects());
    }

    /**
     * Retrieves a learning project by its ID.
     *
     * @param id The ID of the learning project to retrieve.
     * @return A {@link ResponseEntity} containing the requested learning project.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningProjectResource> getLearningProjectById(@PathVariable String id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(learningProjectService.getLearningProjectById(id));
    }

    /**
     * Updates an existing learning project.
     *
     * @param id The ID of the learning project to update.
     * @param projectResource The {@link LearningProjectResource} containing updated details.
     * @return A {@link ResponseEntity} containing the updated learning project.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LearningProjectResource> updateLearningProject(@PathVariable String id, @Valid @RequestBody LearningProjectResource projectResource) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(learningProjectService.updateLearningProject(id, projectResource));
    }

    /**
     * Deletes a learning project by its ID.
     *
     * @param id The ID of the learning project to delete.
     * @return A {@link ResponseEntity} with no content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteLearningProject(@PathVariable String id) {
        learningProjectService.deleteLearningProject(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}