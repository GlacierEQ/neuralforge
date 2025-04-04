package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import com.cenfotec.p3.neuralforge_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    private ProjectRepository projectRepository;

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
        String projectId = "project-123";
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setProjectId(projectId);
        
        ProjectEntity projectEntity = mock(ProjectEntity.class);
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(projectMaterialMapper.mapToEntity(any())).thenReturn(entity);
        when(projectMaterialRepository.save(any())).thenReturn(entity);
        
        // Configure the resource returned by mapToResource with the correct projectId
        ProjectMaterialResource mappedResource = new ProjectMaterialResource();
        mappedResource.setProjectId(projectId);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(mappedResource);

        // Act
        ProjectMaterialResource result = projectMaterialService.createProjectMaterial(resource);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
        verify(projectRepository).findById(projectId);
        verify(projectMaterialMapper).mapToEntity(resource);
        verify(projectMaterialRepository).save(entity);
        verify(projectMaterialMapper).mapToResource(entity);
    }

    @Test
    void updateProjectMaterial_Success() {
        // Arrange
        String id = "material-123";
        String projectId = "project-123";
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setProjectId(projectId);
        resource.setDescription("Updated description");
        resource.setHyperlink("https://example.com");
        
        ProjectMaterialEntity existingEntity = new ProjectMaterialEntity();
        existingEntity.setType("hyperlink");
        
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(projectMaterialRepository.save(any())).thenReturn(existingEntity);
        
        // Configure the resource returned by mapToResource
        ProjectMaterialResource mappedResource = new ProjectMaterialResource();
        mappedResource.setProjectId(projectId);
        mappedResource.setDescription("Updated description");
        when(projectMaterialMapper.mapToResource(any())).thenReturn(mappedResource);

        // Act
        ProjectMaterialResource result = projectMaterialService.updateProjectMaterial(id, resource);

        // Assert
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        verify(projectMaterialRepository).findById(id);
        verify(projectMaterialRepository).save(existingEntity);
        verify(projectMaterialMapper).mapToResource(existingEntity);
    }

    @Test
    void getProjectMaterial_Success() {
        // Arrange
        String id = "material-123";
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setId(id);
        
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(entity));
        when(projectMaterialMapper.mapToResource(entity)).thenReturn(resource);

        // Act
        ProjectMaterialResource result = projectMaterialService.getProjectMaterial(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(projectMaterialRepository).findById(id);
        verify(projectMaterialMapper).mapToResource(entity);
    }

    @Test
    void getAllProjectMaterials_Success() {
        // Arrange
        List<ProjectMaterialEntity> entities = Arrays.asList(new ProjectMaterialEntity());
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setId("material-123");
        
        when(projectMaterialRepository.findAll()).thenReturn(entities);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resource);

        // Act
        List<ProjectMaterialResource> result = projectMaterialService.getAllProjectMaterials();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("material-123", result.get(0).getId());
        verify(projectMaterialRepository).findAll();
        verify(projectMaterialMapper).mapToResource(entities.get(0));
    }

    @Test
    void deleteProjectMaterial_Success() {
        // Arrange
        String id = "material-123";
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        entity.setType("hyperlink"); // Not a file type, so no file deletion needed
        
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        projectMaterialService.deleteMaterial(id);

        // Assert
        verify(projectMaterialRepository).findById(id);
        verify(projectMaterialRepository).delete(entity);
    }

    @Test
    void deleteProjectMaterial_NotFound() {
        // Arrange
        String id = "non-existent-id";
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                                                       () -> projectMaterialService.deleteMaterial(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(projectMaterialRepository).findById(id);
        verify(projectMaterialRepository, never()).delete(any());
    }
    
    @Test
    void getProjectMaterials_Success() {
        // Arrange
        String projectId = "project-123";
        List<ProjectMaterialEntity> entities = Arrays.asList(new ProjectMaterialEntity());
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setProjectId(projectId);
        
        when(projectMaterialRepository.findByProjectId(projectId)).thenReturn(entities);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resource);

        // Act
        List<ProjectMaterialResource> result = projectMaterialService.getProjectMaterials(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectId, result.get(0).getProjectId());
        verify(projectMaterialRepository).findByProjectId(projectId);
        verify(projectMaterialMapper).mapToResource(entities.get(0));
    }
} 