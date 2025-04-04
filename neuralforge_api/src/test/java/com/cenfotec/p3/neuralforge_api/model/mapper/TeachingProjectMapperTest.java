package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for TeachingProjectMapper.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
class TeachingProjectMapperTest {

    @Mock
    private ProjectMaterialMapper projectMaterialMapper;
    
    @Mock
    private SelectedDaysMapper selectedDaysMapper;

    @InjectMocks
    private TeachingProjectMapper teachingProjectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mapToResource_Success() {
        // Arrange
        SelectedDaysEntity selectedDaysEntity = new SelectedDaysEntity();
        selectedDaysEntity.setMonday(true);
        selectedDaysEntity.setWednesday(true);
        
        SelectedDaysResource selectedDaysResource = new SelectedDaysResource();
        selectedDaysResource.setMonday(true);
        selectedDaysResource.setWednesday(true);
        
        ProjectMaterialEntity materialEntity = new ProjectMaterialEntity();
        ProjectMaterialResource materialResource = new ProjectMaterialResource();
        
        List<ProjectMaterialEntity> materialEntities = Arrays.asList(materialEntity);
        List<ProjectMaterialResource> materialResources = Arrays.asList(materialResource);

        TeachingProjectEntity entity = new TeachingProjectEntity();
        entity.setId("123");
        entity.setName("Test Project");
        entity.setDescription("Test Description");
        entity.setCreatedAt(new Date());
        entity.setSelectedDays(selectedDaysEntity);
        entity.setDailyHours(2);
        entity.setWeeksCount(8);
        entity.setMaterials(materialEntities);

        // Mock behavior
        when(selectedDaysMapper.toResource(any(SelectedDaysEntity.class))).thenReturn(selectedDaysResource);
        when(projectMaterialMapper.mapToResource(any(ProjectMaterialEntity.class))).thenReturn(materialResource);

        // Act
        TeachingProjectResource result = teachingProjectMapper.mapToResource(entity);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getDescription(), result.getDescription());
        assertEquals(entity.getCreatedAt(), result.getCreatedAt());
        assertEquals(entity.getDailyHours(), result.getDailyHours());
        assertEquals(entity.getWeeksCount(), result.getWeeksCount());
        assertNotNull(result.getMaterials());
        assertEquals(1, result.getMaterials().size());
        
        // Verify mocks were called
        verify(selectedDaysMapper).toResource(selectedDaysEntity);
        verify(projectMaterialMapper).mapToResource(materialEntity);
    }

    @Test
    void mapToEntity_Success() {
        // Arrange
        SelectedDaysResource selectedDaysResource = new SelectedDaysResource();
        selectedDaysResource.setMonday(true);
        selectedDaysResource.setWednesday(true);
        
        SelectedDaysEntity selectedDaysEntity = new SelectedDaysEntity();
        selectedDaysEntity.setMonday(true);
        selectedDaysEntity.setWednesday(true);
        
        ProjectMaterialResource materialResource = new ProjectMaterialResource();
        ProjectMaterialEntity materialEntity = new ProjectMaterialEntity();
        
        List<ProjectMaterialResource> materialResources = Arrays.asList(materialResource);
        List<ProjectMaterialEntity> materialEntities = Arrays.asList(materialEntity);

        TeachingProjectResource resource = new TeachingProjectResource();
        resource.setId("123");
        resource.setName("Test Project");
        resource.setDescription("Test Description");
        resource.setCreatedAt(new Date());
        resource.setSelectedDays(selectedDaysResource);
        resource.setDailyHours(2);
        resource.setWeeksCount(8);
        resource.setMaterials(materialResources);

        // Mock behavior
        when(selectedDaysMapper.toEntity(any(SelectedDaysResource.class))).thenReturn(selectedDaysEntity);
        when(projectMaterialMapper.mapToEntity(any(ProjectMaterialResource.class))).thenReturn(materialEntity);

        // Act
        TeachingProjectEntity result = teachingProjectMapper.mapToEntity(resource);

        // Assert
        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
        assertEquals(resource.getName(), result.getName());
        assertEquals(resource.getDescription(), result.getDescription());
        assertEquals(resource.getCreatedAt(), result.getCreatedAt());
        assertEquals(resource.getDailyHours(), result.getDailyHours());
        assertEquals(resource.getWeeksCount(), result.getWeeksCount());
        assertNotNull(result.getMaterials());
        assertEquals(1, result.getMaterials().size());
        
        // Verify mocks were called
        verify(selectedDaysMapper).toEntity(selectedDaysResource);
        verify(projectMaterialMapper).mapToEntity(materialResource);
    }
} 