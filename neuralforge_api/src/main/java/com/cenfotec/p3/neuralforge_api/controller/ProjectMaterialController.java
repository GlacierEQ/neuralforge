package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.service.ProjectMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing project materials.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@RestController
@RequestMapping("/api/project-materials")
public class ProjectMaterialController {

    @Autowired
    private ProjectMaterialService projectMaterialService;

    /**
     * Creates a new project material.
     *
     * @param material The project material resource to create.
     * @return The created project material resource.
     */
    @PostMapping
    public ResponseEntity<ProjectMaterialResource> createProjectMaterial(@RequestBody ProjectMaterialResource material) {
        ProjectMaterialResource createdMaterial = projectMaterialService.createProjectMaterial(material);
        return ResponseEntity.ok(createdMaterial);
    }

    /**
     * Updates an existing project material.
     *
     * @param id The ID of the project material to update.
     * @param material The updated project material resource.
     * @return The updated project material resource.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectMaterialResource> updateProjectMaterial(
            @PathVariable Long id,
            @RequestBody ProjectMaterialResource material) {
        ProjectMaterialResource updatedMaterial = projectMaterialService.updateProjectMaterial(id, material);
        return ResponseEntity.ok(updatedMaterial);
    }

    /**
     * Retrieves a project material by its ID.
     *
     * @param id The ID of the project material to retrieve.
     * @return The project material resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectMaterialResource> getProjectMaterial(@PathVariable Long id) {
        ProjectMaterialResource material = projectMaterialService.getProjectMaterial(id);
        return ResponseEntity.ok(material);
    }

    /**
     * Retrieves all project materials.
     *
     * @return A list of project material resources.
     */
    @GetMapping
    public ResponseEntity<List<ProjectMaterialResource>> getAllProjectMaterials() {
        List<ProjectMaterialResource> materials = projectMaterialService.getAllProjectMaterials();
        return ResponseEntity.ok(materials);
    }

    /**
     * Deletes a project material by its ID.
     *
     * @param id The ID of the project material to delete.
     * @return A response with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectMaterial(@PathVariable Long id) {
        projectMaterialService.deleteProjectMaterial(id);
        return ResponseEntity.noContent().build();
    }
} 