package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ProjectMaterialService.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
class ProjectMaterialServiceTest {

    @Mock
    private ProjectMaterialRepository projectMaterialRepository;

    @Mock
    private ProjectMaterialMapper projectMaterialMapper;

    @InjectMocks
    private ProjectMaterialService projectMaterialService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProjectMaterial_Success() {
        // Arrange
        ProjectMaterialResource resource = new ProjectMaterialResource();
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        when(projectMaterialMapper.mapToEntity(any())).thenReturn(entity);
        when(projectMaterialRepository.save(any())).thenReturn(entity);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resource);

        // Act
        ProjectMaterialResource result = projectMaterialService.createProjectMaterial(resource);

        // Assert
        assertNotNull(result);
        verify(projectMaterialMapper).mapToEntity(resource);
        verify(projectMaterialRepository).save(entity);
        verify(projectMaterialMapper).mapToResource(entity);
    }

    @Test
    void updateProjectMaterial_Success() {
        // Arrange
        Long id = 1L;
        ProjectMaterialResource resource = new ProjectMaterialResource();
        ProjectMaterialEntity existingEntity = new ProjectMaterialEntity();
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(projectMaterialRepository.save(any())).thenReturn(existingEntity);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resource);

        // Act
        ProjectMaterialResource result = projectMaterialService.updateProjectMaterial(id, resource);

        // Assert
        assertNotNull(result);
        verify(projectMaterialRepository).findById(id);
        verify(projectMaterialRepository).save(existingEntity);
        verify(projectMaterialMapper).mapToResource(existingEntity);
    }

    @Test
    void getProjectMaterial_Success() {
        // Arrange
        Long id = 1L;
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        ProjectMaterialResource resource = new ProjectMaterialResource();
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(entity));
        when(projectMaterialMapper.mapToResource(entity)).thenReturn(resource);

        // Act
        ProjectMaterialResource result = projectMaterialService.getProjectMaterial(id);

        // Assert
        assertNotNull(result);
        verify(projectMaterialRepository).findById(id);
        verify(projectMaterialMapper).mapToResource(entity);
    }

    @Test
    void getAllProjectMaterials_Success() {
        // Arrange
        List<ProjectMaterialEntity> entities = Arrays.asList(new ProjectMaterialEntity());
        List<ProjectMaterialResource> resources = Arrays.asList(new ProjectMaterialResource());
        when(projectMaterialRepository.findAll()).thenReturn(entities);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resources.get(0));

        // Act
        List<ProjectMaterialResource> result = projectMaterialService.getAllProjectMaterials();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectMaterialRepository).findAll();
        verify(projectMaterialMapper).mapToResource(entities.get(0));
    }

    @Test
    void deleteProjectMaterial_Success() {
        // Arrange
        Long id = 1L;
        when(projectMaterialRepository.existsById(id)).thenReturn(true);

        // Act
        projectMaterialService.deleteProjectMaterial(id);

        // Assert
        verify(projectMaterialRepository).existsById(id);
        verify(projectMaterialRepository).deleteById(id);
    }

    @Test
    void deleteProjectMaterial_NotFound() {
        // Arrange
        Long id = 1L;
        when(projectMaterialRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectMaterialService.deleteProjectMaterial(id));
        verify(projectMaterialRepository).existsById(id);
        verify(projectMaterialRepository, never()).deleteById(id);
    }
} 