package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.service.TeachingProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing teaching projects.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@RestController
@RequestMapping("/teaching-projects")
public class TeachingProjectController {

    @Autowired
    private TeachingProjectService teachingProjectService;

    /**
     * Creates a new teaching project.
     *
     * @param teachingProject The teaching project resource to create.
     * @return The created teaching project resource.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeachingProjectResource> createTeachingProject(@Valid @RequestBody TeachingProjectResource teachingProject) {
        TeachingProjectResource createdProject = teachingProjectService.createTeachingProject(teachingProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Updates an existing teaching project.
     *
     * @param id The ID of the teaching project to update.
     * @param teachingProject The updated teaching project resource.
     * @return The updated teaching project resource.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeachingProjectResource> updateTeachingProject(
            @PathVariable String id,
            @Valid @RequestBody TeachingProjectResource teachingProject) {
        TeachingProjectResource updatedProject = teachingProjectService.updateTeachingProject(id, teachingProject);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Retrieves a teaching project by its ID.
     *
     * @param id The ID of the teaching project to retrieve.
     * @return The teaching project resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeachingProjectResource> getTeachingProject(@PathVariable String id) {
        TeachingProjectResource project = teachingProjectService.getTeachingProject(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Retrieves all teaching projects.
     *
     * @return A list of teaching project resources.
     */
    @GetMapping
    public ResponseEntity<List<TeachingProjectResource>> getAllTeachingProjects() {
        List<TeachingProjectResource> projects = teachingProjectService.getAllTeachingProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Retrieves all teaching projects created by the currently authenticated user.
     *
     * @return A list of teaching project resources.
     */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeachingProjectResource>> getCurrentUserTeachingProjects() {
        List<TeachingProjectResource> projects = teachingProjectService.getCurrentUserTeachingProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Deletes a teaching project by its ID.
     *
     * @param id The ID of the teaching project to delete.
     * @return A response with no content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTeachingProject(@PathVariable String id) {
        teachingProjectService.deleteTeachingProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a material from a teaching project.
     *
     * @param projectId The ID of the teaching project.
     * @param materialId The ID of the material to delete.
     * @return A response with no content.
     */
    @DeleteMapping("/{projectId}/materials/{materialId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable String projectId,
            @PathVariable String materialId) {
        teachingProjectService.deleteMaterial(projectId, materialId);
        return ResponseEntity.noContent().build();
    }
} 