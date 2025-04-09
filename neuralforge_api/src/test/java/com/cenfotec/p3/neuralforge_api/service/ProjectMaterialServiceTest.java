package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import com.cenfotec.p3.neuralforge_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * Updated to properly mock SecurityContext and UserEntity roles.
 */
class ProjectMaterialServiceTest {

    @Mock
    private ProjectMaterialRepository projectMaterialRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMaterialMapper projectMaterialMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserEntity mockUser;

    @InjectMocks
    private ProjectMaterialService projectMaterialService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        // Setup mock user ID and role
        when(mockUser.getId()).thenReturn("user-123");
        var mockRole = mock(UserRoleEntity.class);
        when(mockRole.getName()).thenReturn(UserRoleEnum.ROLE_ADMINISTRATOR);
        when(mockUser.getRole()).thenReturn(mockRole);
    }

    @Test
    void createProjectMaterial_Success() {
        String projectId = "project-123";
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setProjectId(projectId);

        ProjectEntity projectEntity = new TeachingProjectEntity();
        projectEntity.setCreatorUserId("user-123");

        ProjectMaterialEntity entity = new ProjectMaterialEntity();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(projectMaterialMapper.mapToEntity(any())).thenReturn(entity);
        when(projectMaterialRepository.save(any())).thenReturn(entity);

        ProjectMaterialResource mappedResource = new ProjectMaterialResource();
        mappedResource.setProjectId(projectId);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(mappedResource);

        ProjectMaterialResource result = projectMaterialService.createProjectMaterial(resource);

        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
    }

    @Test
    void updateProjectMaterial_Success() {
        String id = "material-123";
        String projectId = "project-123";
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setProjectId(projectId);
        resource.setDescription("Updated description");
        resource.setHyperlink("https://example.com");

        ProjectEntity project = new TeachingProjectEntity();
        project.setCreatorUserId("user-123");

        ProjectMaterialEntity existingEntity = new ProjectMaterialEntity();
        existingEntity.setType("hyperlink");
        existingEntity.setProject(project);

        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(projectMaterialRepository.save(any())).thenReturn(existingEntity);

        ProjectMaterialResource mappedResource = new ProjectMaterialResource();
        mappedResource.setProjectId(projectId);
        mappedResource.setDescription("Updated description");
        when(projectMaterialMapper.mapToResource(any())).thenReturn(mappedResource);

        ProjectMaterialResource result = projectMaterialService.updateProjectMaterial(id, resource);

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
    }

    @Test
    void getProjectMaterial_Success() {
        String id = "material-123";
        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setId(id);

        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(entity));
        when(projectMaterialMapper.mapToResource(entity)).thenReturn(resource);

        ProjectMaterialResource result = projectMaterialService.getProjectMaterial(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getAllProjectMaterials_Success() {
        List<ProjectMaterialEntity> entities = Arrays.asList(new ProjectMaterialEntity());
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setId("material-123");

        when(projectMaterialRepository.findAll()).thenReturn(entities);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resource);

        List<ProjectMaterialResource> result = projectMaterialService.getAllProjectMaterials();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("material-123", result.get(0).getId());
    }

    @Test
    void deleteProjectMaterial_Success() {
        String id = "material-123";

        ProjectEntity project = new TeachingProjectEntity();
        project.setCreatorUserId("user-123");

        ProjectMaterialEntity entity = new ProjectMaterialEntity();
        entity.setType("hyperlink");
        entity.setProject(project);

        when(projectMaterialRepository.findById(id)).thenReturn(Optional.of(entity));

        projectMaterialService.deleteMaterial(id);

        verify(projectMaterialRepository).delete(entity);
    }

    @Test
    void deleteProjectMaterial_NotFound() {
        String id = "non-existent-id";
        when(projectMaterialRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> projectMaterialService.deleteMaterial(id));

        assertEquals(404, exception.getStatusCode().value());
        verify(projectMaterialRepository, never()).delete(any());
    }

    @Test
    void getProjectMaterials_Success() {
        String projectId = "project-123";
        List<ProjectMaterialEntity> entities = Arrays.asList(new ProjectMaterialEntity());
        ProjectMaterialResource resource = new ProjectMaterialResource();
        resource.setProjectId(projectId);

        when(projectMaterialRepository.findByProjectId(projectId)).thenReturn(entities);
        when(projectMaterialMapper.mapToResource(any())).thenReturn(resource);

        List<ProjectMaterialResource> result = projectMaterialService.getProjectMaterials(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectId, result.get(0).getProjectId());
    }
}
