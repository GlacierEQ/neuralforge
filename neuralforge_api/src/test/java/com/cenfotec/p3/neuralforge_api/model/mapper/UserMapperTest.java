package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper mockUserRoleMapper;

    @Spy
    private UserRoleMapper spyUserRoleMapper = new UserRoleMapper();

    private UserEntity mockUserEntity;
    private UserResource mockUserResource;
    private UserRoleEntity mockUserRoleEntity;
    private UserRoleResource mockUserRoleResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUserRoleEntity = new UserRoleEntity();
        mockUserRoleEntity.setName(UserRoleEnum.ROLE_STUDENT);

        mockUserRoleResource = new UserRoleResource();
        mockUserRoleResource.setName(UserRoleEnum.ROLE_STUDENT);

        LocalDateTime now = LocalDateTime.now();

        mockUserEntity = UserEntity.builder()
                .id("123")
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securePassword")
                .status(true)
                .createdAt(LocalDateTime.now())
                .role(mockUserRoleEntity)
                .lastPasswordChangeAt(now)
                .build();

        mockUserResource = UserResource.builder()
                .id("123")
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securePassword")
                .status(true)
                .createdAt(LocalDateTime.now())
                .role(mockUserRoleResource)
                .lastPasswordChangeAt(now)
                .build();
    }

    @Test
    void givenUserEntity_whenMapToResource_thenReturnUserResource() {
        // Given
        lenient().when(mockUserRoleMapper.mapToResource(mockUserRoleEntity)).thenReturn(mockUserRoleResource);

        // When
        UserResource result = userMapper.mapToResource(mockUserEntity);

        // Then
        assertNotNull(result);
        assertEquals(mockUserEntity.getId(), result.getId());
        assertEquals(mockUserEntity.getName(), result.getName());
        assertEquals(mockUserEntity.getLastName(), result.getLastName());
        assertEquals(mockUserEntity.getEmail(), result.getEmail());
        assertEquals(mockUserEntity.getStatus(), result.getStatus());
        assertEquals(mockUserEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(mockUserRoleResource, result.getRole());
        assertEquals(mockUserEntity.getLastPasswordChangeAt(), result.getLastPasswordChangeAt());
    }

    @Test
    void givenUserResource_whenMapToEntity_thenReturnUserEntity() {
        // Given
        lenient().doReturn(mockUserRoleEntity).when(spyUserRoleMapper).mapToEntity(mockUserRoleResource);

        // When
        UserEntity result = userMapper.mapToEntity(mockUserResource);

        // Then
        assertNotNull(result);
        assertEquals(mockUserResource.getId(), result.getId());
        assertEquals(mockUserResource.getName(), result.getName());
        assertEquals(mockUserResource.getLastName(), result.getLastName());
        assertEquals(mockUserResource.getEmail(), result.getEmail());
        assertEquals(mockUserResource.getStatus(), result.getStatus());
        assertEquals(mockUserResource.getCreatedAt(), result.getCreatedAt());
        assertEquals(mockUserResource.getPassword(), result.getPassword());
        assertEquals(mockUserRoleEntity, result.getRole());
        assertEquals(mockUserResource.getLastPasswordChangeAt(), result.getLastPasswordChangeAt());
    }
}
