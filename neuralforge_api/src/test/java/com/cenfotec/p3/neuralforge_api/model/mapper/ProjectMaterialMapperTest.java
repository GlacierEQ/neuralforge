package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ProjectMaterialMapper.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
class ProjectMaterialMapperTest {

    @InjectMocks
    private ProjectMaterialMapper projectMaterialMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mapToResource_Success() {
        // Arrange
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        entity.setId(1L);
        entity.setType("PDF");
        entity.setFileName("test.pdf");
        entity.setFileUrl("http://example.com/test.pdf");
        entity.setDescription("Test Description");
        entity.setHyperlink("http://example.com");

        // Act
        ProjectMaterialResource result = projectMaterialMapper.mapToResource(entity);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getType(), result.getType());
        assertEquals(entity.getFileName(), result.getFileName());
        assertEquals(entity.getFileUrl(), result.getFileUrl());
        assertEquals(entity.getDescription(), result.getDescription());
        assertEquals(entity.getHyperlink(), result.getHyperlink());
    }

    @Test
    void mapToEntity_Success() {
        // Arrange
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setId(1L);
        resource.setType("PDF");
        resource.setFileName("test.pdf");
        resource.setFileUrl("http://example.com/test.pdf");
        resource.setDescription("Test Description");
        resource.setHyperlink("http://example.com");

        // Act
        ProjectMaterialEntity result = projectMaterialMapper.mapToEntity(resource);

        // Assert
        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
        assertEquals(resource.getType(), result.getType());
        assertEquals(resource.getFileName(), result.getFileName());
        assertEquals(resource.getFileUrl(), result.getFileUrl());
        assertEquals(resource.getDescription(), result.getDescription());
        assertEquals(resource.getHyperlink(), result.getHyperlink());
    }
} 