package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
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

    @InjectMocks
    private TeachingProjectMapper teachingProjectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mapToResource_Success() {
        // Arrange
        TeachingProjectEntity entity = new TeachingProjectEntity();
        entity.setId(1L);
        entity.setName("Test Project");
        entity.setDescription("Test Description");
        entity.setCreatedAt(new Date());
        entity.setSelectedDays("Monday,Wednesday");
        entity.setDailyHours(2);
        entity.setWeeksCount(8);
        entity.setMaterials(Arrays.asList());

        // Act
        TeachingProjectResource result = teachingProjectMapper.mapToResource(entity);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getDescription(), result.getDescription());
        assertEquals(entity.getCreatedAt(), result.getCreatedAt());
        assertEquals(entity.getSelectedDays(), result.getSelectedDays());
        assertEquals(entity.getDailyHours(), result.getDailyHours());
        assertEquals(entity.getWeeksCount(), result.getWeeksCount());
        assertNotNull(result.getMaterials());
    }

    @Test
    void mapToEntity_Success() {
        // Arrange
        TeachingProjectResource resource = new TeachingProjectResource();
        resource.setId(1L);
        resource.setName("Test Project");
        resource.setDescription("Test Description");
        resource.setCreatedAt(new Date());
        resource.setSelectedDays("Monday,Wednesday");
        resource.setDailyHours(2);
        resource.setWeeksCount(8);
        resource.setMaterials(Arrays.asList());

        // Act
        TeachingProjectEntity result = teachingProjectMapper.mapToEntity(resource);

        // Assert
        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
        assertEquals(resource.getName(), result.getName());
        assertEquals(resource.getDescription(), result.getDescription());
        assertEquals(resource.getCreatedAt(), result.getCreatedAt());
        assertEquals(resource.getSelectedDays(), result.getSelectedDays());
        assertEquals(resource.getDailyHours(), result.getDailyHours());
        assertEquals(resource.getWeeksCount(), result.getWeeksCount());
        assertNotNull(result.getMaterials());
    }
} 