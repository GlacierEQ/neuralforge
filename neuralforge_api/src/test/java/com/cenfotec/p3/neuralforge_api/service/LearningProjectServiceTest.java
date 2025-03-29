package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.LearningProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.LearningProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.LearningProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.LearningProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningProjectServiceTest {

    @InjectMocks
    private LearningProjectService learningProjectService;

    @Mock
    private LearningProjectRepository learningProjectRepository;

    @Mock
    private LearningProjectMapper learningProjectMapper;

    private LearningProjectEntity mockLearningProjectEntity;
    private LearningProjectResource mockLearningProjectResource;
    private UserEntity mockUserEntity;

    @BeforeEach
    void setUp() {
        mockUserEntity = new UserEntity();
        mockUserEntity.setId("user123");
        mockUserEntity.setEmail("test@example.com");

        mockLearningProjectEntity = LearningProjectEntity.builder()
                .id("project123")
                .creatorUserId(mockUserEntity.getId())
                .name("Test Learning Project")
                .description("This is a test learning project")
                .build();
        mockLearningProjectEntity.setProjectType(ProjectTypeEnum.LEARNING);

        mockLearningProjectResource = LearningProjectResource.builder()
                .id("project123")
                .creatorUserId(mockUserEntity.getId())
                .name("Test Learning Project")
                .description("This is a test learning project")
                .projectType(ProjectTypeEnum.LEARNING)
                .build();
    }

    @Test
    void givenValidDetails_whenCreateLearningProject_thenReturnCreatedProject() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserEntity);
        SecurityContextHolder.setContext(securityContext);

        when(learningProjectMapper.mapToEntity(any(LearningProjectResource.class))).thenReturn(mockLearningProjectEntity);
        when(learningProjectRepository.save(any(LearningProjectEntity.class))).thenReturn(mockLearningProjectEntity);
        when(learningProjectMapper.mapToResource(any(LearningProjectEntity.class))).thenReturn(mockLearningProjectResource);

        // When
        LearningProjectResource result = learningProjectService.createLearningProject(mockLearningProjectResource);

        // Then
        assertNotNull(result);
        assertEquals(mockLearningProjectResource.getName(), result.getName());
        assertEquals(mockUserEntity.getId(), result.getCreatorUserId());

        verify(learningProjectMapper, times(1)).mapToEntity(any(LearningProjectResource.class));
        verify(learningProjectRepository, times(1)).save(any(LearningProjectEntity.class));
        verify(learningProjectMapper, times(1)).mapToResource(any(LearningProjectEntity.class));

        // Clean up security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenGetAllLearningProjects_thenReturnProjectList() {
        // Given
        List<LearningProjectEntity> mockProjects = Arrays.asList(mockLearningProjectEntity);
        when(learningProjectRepository.findAll()).thenReturn(mockProjects);
        when(learningProjectMapper.mapToResource(any(LearningProjectEntity.class))).thenReturn(mockLearningProjectResource);

        // When
        List<LearningProjectResource> result = learningProjectService.getAllLearningProjects();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockLearningProjectResource.getName(), result.get(0).getName());

        verify(learningProjectRepository, times(1)).findAll();
        verify(learningProjectMapper, times(1)).mapToResource(any(LearningProjectEntity.class));
    }

    @Test
    void whenGetCurrentUserLearningProjects_thenReturnUserProjectList() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserEntity);
        SecurityContextHolder.setContext(securityContext);

        List<LearningProjectEntity> mockProjects = Arrays.asList(mockLearningProjectEntity);
        when(learningProjectRepository.findByCreatorUserId(mockUserEntity.getId())).thenReturn(mockProjects);
        when(learningProjectMapper.mapToResource(any(LearningProjectEntity.class))).thenReturn(mockLearningProjectResource);

        // When
        List<LearningProjectResource> result = learningProjectService.getCurrentUserLearningProjects();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockLearningProjectResource.getName(), result.get(0).getName());

        verify(learningProjectRepository, times(1)).findByCreatorUserId(mockUserEntity.getId());
        verify(learningProjectMapper, times(1)).mapToResource(any(LearningProjectEntity.class));

        // Clean up security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenValidId_whenGetLearningProjectById_thenReturnProject() {
        // Given
        when(learningProjectRepository.findById("project123")).thenReturn(Optional.of(mockLearningProjectEntity));
        when(learningProjectMapper.mapToResource(mockLearningProjectEntity)).thenReturn(mockLearningProjectResource);

        // When
        LearningProjectResource result = learningProjectService.getLearningProjectById("project123");

        // Then
        assertNotNull(result);
        assertEquals(mockLearningProjectResource.getId(), result.getId());
        assertEquals(mockLearningProjectResource.getName(), result.getName());

        verify(learningProjectRepository, times(1)).findById("project123");
        verify(learningProjectMapper, times(1)).mapToResource(mockLearningProjectEntity);
    }

    @Test
    void givenInvalidId_whenGetLearningProjectById_thenThrowException() {
        // Given
        when(learningProjectRepository.findById("invalidId")).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> learningProjectService.getLearningProjectById("invalidId"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Learning project not found"));

        verify(learningProjectRepository, times(1)).findById("invalidId");
        verifyNoInteractions(learningProjectMapper);
    }

    @Test
    void givenValidId_whenUpdateLearningProject_thenReturnUpdatedProject() {
        // Given
        when(learningProjectRepository.findById("project123")).thenReturn(Optional.of(mockLearningProjectEntity));
        when(learningProjectMapper.mapToEntity(any(LearningProjectResource.class))).thenReturn(mockLearningProjectEntity);
        when(learningProjectRepository.save(any(LearningProjectEntity.class))).thenReturn(mockLearningProjectEntity);
        when(learningProjectMapper.mapToResource(mockLearningProjectEntity)).thenReturn(mockLearningProjectResource);

        LearningProjectResource updatedResource = LearningProjectResource.builder()
                .name("Updated Test Project")
                .description("Updated description")
                .build();

        // When
        LearningProjectResource result = learningProjectService.updateLearningProject("project123", updatedResource);

        // Then
        assertNotNull(result);
        assertEquals("project123", result.getId());
        assertEquals(mockLearningProjectResource.getName(), result.getName());

        verify(learningProjectRepository, times(1)).findById("project123");
        verify(learningProjectMapper, times(1)).mapToEntity(any(LearningProjectResource.class));
        verify(learningProjectRepository, times(1)).save(any(LearningProjectEntity.class));
        verify(learningProjectMapper, times(1)).mapToResource(mockLearningProjectEntity);
    }

    @Test
    void givenInvalidId_whenUpdateLearningProject_thenThrowException() {
        // Given
        when(learningProjectRepository.findById("invalidId")).thenReturn(Optional.empty());

        LearningProjectResource updatedResource = LearningProjectResource.builder()
                .name("Updated Test Project")
                .description("Updated description")
                .build();

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> learningProjectService.updateLearningProject("invalidId", updatedResource));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Learning project not found"));

        verify(learningProjectRepository, times(1)).findById("invalidId");
        verifyNoInteractions(learningProjectMapper);
    }

    @Test
    void givenValidId_whenDeleteLearningProject_thenDeleteSuccessfully() {
        // Given
        when(learningProjectRepository.existsById("project123")).thenReturn(true);
        doNothing().when(learningProjectRepository).deleteById("project123");

        // When
        learningProjectService.deleteLearningProject("project123");

        // Then
        verify(learningProjectRepository, times(1)).existsById("project123");
        verify(learningProjectRepository, times(1)).deleteById("project123");
    }

    @Test
    void givenInvalidId_whenDeleteLearningProject_thenThrowException() {
        // Given
        when(learningProjectRepository.existsById("invalidId")).thenReturn(false);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> learningProjectService.deleteLearningProject("invalidId"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Learning project not found"));

        verify(learningProjectRepository, times(1)).existsById("invalidId");
        verify(learningProjectRepository, never()).deleteById(anyString());
    }
}