package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.TeachingProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.TeachingProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for TeachingProjectService.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
class TeachingProjectServiceTest {

    @Mock
    private TeachingProjectRepository teachingProjectRepository;

    @Mock
    private TeachingProjectMapper teachingProjectMapper;

    @InjectMocks
    private TeachingProjectService teachingProjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTeachingProject_Success() {
        // Arrange
        TeachingProjectResource resource = new TeachingProjectResource();
        TeachingProjectEntity entity = new TeachingProjectEntity();
        when(teachingProjectMapper.mapToEntity(any())).thenReturn(entity);
        when(teachingProjectRepository.save(any())).thenReturn(entity);
        when(teachingProjectMapper.mapToResource(any())).thenReturn(resource);

        // Act
        TeachingProjectResource result = teachingProjectService.createTeachingProject(resource);

        // Assert
        assertNotNull(result);
        verify(teachingProjectMapper).mapToEntity(resource);
        verify(teachingProjectRepository).save(entity);
        verify(teachingProjectMapper).mapToResource(entity);
    }

    @Test
    void updateTeachingProject_Success() {
        // Arrange
        Long id = 1L;
        TeachingProjectResource resource = new TeachingProjectResource();
        TeachingProjectEntity existingEntity = new TeachingProjectEntity();
        when(teachingProjectRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(teachingProjectRepository.save(any())).thenReturn(existingEntity);
        when(teachingProjectMapper.mapToResource(any())).thenReturn(resource);

        // Act
        TeachingProjectResource result = teachingProjectService.updateTeachingProject(id, resource);

        // Assert
        assertNotNull(result);
        verify(teachingProjectRepository).findById(id);
        verify(teachingProjectRepository).save(existingEntity);
        verify(teachingProjectMapper).mapToResource(existingEntity);
    }

    @Test
    void getTeachingProject_Success() {
        // Arrange
        Long id = 1L;
        TeachingProjectEntity entity = new TeachingProjectEntity();
        TeachingProjectResource resource = new TeachingProjectResource();
        when(teachingProjectRepository.findById(id)).thenReturn(Optional.of(entity));
        when(teachingProjectMapper.mapToResource(entity)).thenReturn(resource);

        // Act
        TeachingProjectResource result = teachingProjectService.getTeachingProject(id);

        // Assert
        assertNotNull(result);
        verify(teachingProjectRepository).findById(id);
        verify(teachingProjectMapper).mapToResource(entity);
    }

    @Test
    void getAllTeachingProjects_Success() {
        // Arrange
        List<TeachingProjectEntity> entities = Arrays.asList(new TeachingProjectEntity());
        List<TeachingProjectResource> resources = Arrays.asList(new TeachingProjectResource());
        when(teachingProjectRepository.findAll()).thenReturn(entities);
        when(teachingProjectMapper.mapToResource(any())).thenReturn(resources.get(0));

        // Act
        List<TeachingProjectResource> result = teachingProjectService.getAllTeachingProjects();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(teachingProjectRepository).findAll();
        verify(teachingProjectMapper).mapToResource(entities.get(0));
    }

    @Test
    void deleteTeachingProject_Success() {
        // Arrange
        Long id = 1L;
        when(teachingProjectRepository.existsById(id)).thenReturn(true);

        // Act
        teachingProjectService.deleteTeachingProject(id);

        // Assert
        verify(teachingProjectRepository).existsById(id);
        verify(teachingProjectRepository).deleteById(id);
    }

    @Test
    void uploadFile_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        // Act
        String result = teachingProjectService.uploadFile(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith("_test.txt"));
    }
} 