package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.UserProjectsResource;
import com.cenfotec.p3.neuralforge_api.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling combined project requests.
 * Provides endpoints for retrieving all types of projects in a single call.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * Retrieves all types of projects (Learning, Teaching, ProgrammedGoal) for the current user in a single call.
     *
     * @return A {@link ResponseEntity} containing all projects for the current user.
     */
    @GetMapping("/all-mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProjectsResource> getAllUserProjects() {
        return ResponseEntity.ok(projectService.getAllUserProjects());
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserProjectsResource> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }
} 