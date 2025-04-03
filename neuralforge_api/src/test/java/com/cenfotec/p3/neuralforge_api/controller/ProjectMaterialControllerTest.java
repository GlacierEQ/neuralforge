package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.service.ProjectMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ProjectMaterialController.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
class ProjectMaterialControllerTest {

    @Mock
    private ProjectMaterialService projectMaterialService;

    @InjectMocks
    private ProjectMaterialController projectMaterialController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProjectMaterial_Success() {
        // Arrange
        ProjectMaterialResource resource = new ProjectMaterialResource();
        when(projectMaterialService.createProjectMaterial(any())).thenReturn(resource);

        // Act
        ResponseEntity<ProjectMaterialResource> response = projectMaterialController.createProjectMaterial(resource);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(projectMaterialService).createProjectMaterial(resource);
    }

    @Test
    void updateProjectMaterial_Success() {
        // Arrange
        Long id = 1L;
        ProjectMaterialResource resource = new ProjectMaterialResource();
        when(projectMaterialService.updateProjectMaterial(eq(id), any())).thenReturn(resource);

        // Act
        ResponseEntity<ProjectMaterialResource> response = projectMaterialController.updateProjectMaterial(id, resource);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(projectMaterialService).updateProjectMaterial(id, resource);
    }

    @Test
    void getProjectMaterial_Success() {
        // Arrange
        Long id = 1L;
        ProjectMaterialResource resource = new ProjectMaterialResource();
        when(projectMaterialService.getProjectMaterial(id)).thenReturn(resource);

        // Act
        ResponseEntity<ProjectMaterialResource> response = projectMaterialController.getProjectMaterial(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(projectMaterialService).getProjectMaterial(id);
    }

    @Test
    void getAllProjectMaterials_Success() {
        // Arrange
        List<ProjectMaterialResource> resources = Arrays.asList(new ProjectMaterialResource());
        when(projectMaterialService.getAllProjectMaterials()).thenReturn(resources);

        // Act
        ResponseEntity<List<ProjectMaterialResource>> response = projectMaterialController.getAllProjectMaterials();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(projectMaterialService).getAllProjectMaterials();
    }

    @Test
    void deleteProjectMaterial_Success() {
        // Arrange
        Long id = 1L;
        doNothing().when(projectMaterialService).deleteProjectMaterial(id);

        // Act
        ResponseEntity<Void> response = projectMaterialController.deleteProjectMaterial(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(projectMaterialService).deleteProjectMaterial(id);
    }
} 