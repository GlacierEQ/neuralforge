package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserRoleMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @InjectMocks
    private UserRoleService userRoleService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    private UserRoleEntity mockRoleEntity;
    private UserRoleResource mockRoleResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockRoleEntity = new UserRoleEntity();
        mockRoleEntity.setName(UserRoleEnum.ROLE_STUDENT);

        mockRoleResource = new UserRoleResource();
        mockRoleResource.setName(UserRoleEnum.ROLE_STUDENT);
    }

    @Test
    void givenExistingRole_whenGetRoleByEnum_thenReturnUserRoleResource() {
        // Given
        when(userRoleRepository.findByName(UserRoleEnum.ROLE_STUDENT)).thenReturn(Optional.of(mockRoleEntity));

        // When
        UserRoleResource result = userRoleService.getRoleByEnum(UserRoleEnum.ROLE_STUDENT);

        // Then
        assertNotNull(result);
        assertTrue(UserRoleEnum.ROLE_STUDENT.name().equals(result.getName().toString()));

        verify(userRoleRepository, times(1)).findByName(UserRoleEnum.ROLE_STUDENT);
    }

    @Test
    void givenNonExistingRole_whenGetRoleByEnum_thenThrowException() {
        // Given
        when(userRoleRepository.findByName(UserRoleEnum.ROLE_ADMINISTRATOR)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userRoleService.getRoleByEnum(UserRoleEnum.ROLE_ADMINISTRATOR));

        assertEquals("404 NOT_FOUND \"Role not found\"", exception.getMessage());

        verify(userRoleRepository, times(1)).findByName(UserRoleEnum.ROLE_ADMINISTRATOR);
        verify(userRoleMapper, never()).mapToResource(any());
    }
}
