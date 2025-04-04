package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.SelectedDaysMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.TeachingProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.TeachingProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private UserEntity mockUser;
    
    @Mock
    private SelectedDaysService selectedDaysService;
    
    @Mock
    private SelectedDaysMapper selectedDaysMapper;

    @InjectMocks
    private TeachingProjectService teachingProjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup security context mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // Setup authentication mock
        when(authentication.getPrincipal()).thenReturn(mockUser);
        
        // Setup user mock
        when(mockUser.getId()).thenReturn("user-123");
        
        // Setup SelectedDaysService mock
        SelectedDaysEntity savedSelectedDays = new SelectedDaysEntity();
        when(selectedDaysService.save(any(SelectedDaysResource.class))).thenReturn(savedSelectedDays);
        
        // Setup SelectedDaysMapper mock
        when(selectedDaysMapper.toResource(any(SelectedDaysEntity.class))).thenReturn(new SelectedDaysResource());
    }
    
    @AfterEach
    void tearDown() {
        // Clean up security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTeachingProject_Success() {
        // Arrange
        TeachingProjectResource resource = new TeachingProjectResource();
        resource.setSelectedDays(new SelectedDaysResource());
        
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
        verify(selectedDaysService).save(any(SelectedDaysResource.class));
    }

    @Test
    void updateTeachingProject_Success() {
        // Arrange
        String id = "1";
        TeachingProjectResource resource = new TeachingProjectResource();
        resource.setSelectedDays(new SelectedDaysResource());
        
        TeachingProjectEntity existingEntity = new TeachingProjectEntity();
        existingEntity.setCreatorUserId("user-123"); // Match the mocked user ID
        
        when(teachingProjectRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(teachingProjectRepository.save(any())).thenReturn(existingEntity);
        when(teachingProjectMapper.mapToResource(any())).thenReturn(resource);
        when(teachingProjectMapper.mapToEntity(any())).thenReturn(existingEntity);

        // Act
        TeachingProjectResource result = teachingProjectService.updateTeachingProject(id, resource);

        // Assert
        assertNotNull(result);
        verify(teachingProjectRepository).findById(id);
        verify(teachingProjectRepository).save(existingEntity);
        verify(teachingProjectMapper).mapToResource(existingEntity);
        verify(selectedDaysService).save(any(SelectedDaysResource.class));
    }

    @Test
    void getTeachingProject_Success() {
        // Arrange
        String id = "1";
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
        String id = "1";
        TeachingProjectEntity entity = new TeachingProjectEntity();
        entity.setCreatorUserId("user-123"); // Match the mocked user ID
        
        when(teachingProjectRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        teachingProjectService.deleteTeachingProject(id);

        // Assert
        verify(teachingProjectRepository).findById(id);
        verify(teachingProjectRepository).deleteById(id);
    }
}