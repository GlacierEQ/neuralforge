package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProgrammedGoalProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.ProgrammedGoalProjectRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgrammedGoalProjectServiceTest {

    @InjectMocks
    private ProgrammedGoalProjectService service;

    @Mock
    private SelectedDaysService selectedDaysService;

    @Mock
    private ProgrammedGoalProjectRepository repository;

    @Mock
    private ProgrammedGoalProjectMapper mapper;

    private UserEntity mockUser;
    private ProgrammedGoalProjectEntity mockEntity;
    private ProgrammedGoalProjectResource mockResource;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId("user123");
        mockUser.setRole(UserRoleEntity.builder().id("123").name(UserRoleEnum.ROLE_STUDENT).build());

        mockEntity = new ProgrammedGoalProjectEntity();
        mockEntity.setId("proj123");
        mockEntity.setCreatorUserId("user123");
        mockEntity.setNotify(true);

        mockResource = new ProgrammedGoalProjectResource();
        mockResource.setId("proj123");
        mockResource.setCreatorUserId("user123");
    }

    void mockAuthentication() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void whenGetAll_thenReturnList() {
        when(repository.findAll()).thenReturn(Collections.singletonList(mockEntity));
        when(mapper.mapToResource(mockEntity)).thenReturn(mockResource);

        assertEquals(1, service.getAllProgrammedGoalProjects().size());
    }

    @Test
    void whenGetById_thenReturnResource() {
        mockAuthentication();

        when(repository.findById("proj123")).thenReturn(Optional.of(mockEntity));
        when(mapper.mapToResource(mockEntity)).thenReturn(mockResource);

        ProgrammedGoalProjectResource result = service.getProgrammedGoalProjectById("proj123");

        assertNotNull(result);
        assertEquals("proj123", result.getId());

        SecurityContextHolder.clearContext(); // Clean up
    }


    @Test
    void whenGetByInvalidId_thenThrow() {
        when(repository.findById("invalid")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getProgrammedGoalProjectById("invalid"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenGetCurrentUserProjects_thenReturnList() {
        mockAuthentication();
        when(repository.findByCreatorUserId("user123")).thenReturn(Collections.singletonList(mockEntity));
        when(mapper.mapToResource(mockEntity)).thenReturn(mockResource);

        assertEquals(1, service.getCurrentUserProgrammedGoalProjects().size());
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenToggleNotifications_thenToggleValue() {
        mockAuthentication();
        when(repository.findById("proj123")).thenReturn(Optional.of(mockEntity));
        when(repository.save(any())).thenReturn(mockEntity);
        when(mapper.mapToResource(any())).thenReturn(mockResource);

        ProgrammedGoalProjectResource result = service.toggleNotifications("proj123");
        assertNotNull(result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void whenDeleteWithValidUser_thenDelete() {
        mockAuthentication();
        when(repository.findById("proj123")).thenReturn(Optional.of(mockEntity));
        doNothing().when(repository).deleteById("proj123");

        service.deleteProgrammedGoalProject("proj123");
        verify(repository, times(1)).deleteById("proj123");

        SecurityContextHolder.clearContext();
    }

    @Test
    void whenDeleteInvalidId_thenThrow() {
        when(repository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.deleteProgrammedGoalProject("invalid"));
    }
}
