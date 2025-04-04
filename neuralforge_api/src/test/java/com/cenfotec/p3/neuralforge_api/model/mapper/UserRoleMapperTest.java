package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleMapperTest {

    private UserRoleMapper userRoleMapper;

    private UserRoleEntity mockUserRoleEntity;
    private UserRoleResource mockUserRoleResource;

    @BeforeEach
    void setUp() {
        userRoleMapper = new UserRoleMapper();

        mockUserRoleEntity = UserRoleEntity.builder()
                .id("1")
                .name(UserRoleEnum.ROLE_STUDENT)
                .description("Student role")
                .build();

        mockUserRoleResource = UserRoleResource.builder()
                .id("1")
                .name(UserRoleEnum.ROLE_STUDENT)
                .description("Student role")
                .build();
    }

    @Test
    void givenUserRoleEntity_whenMapToResource_thenReturnUserRoleResource() {
        // When
        UserRoleResource result = userRoleMapper.mapToResource(mockUserRoleEntity);

        // Then
        assertNotNull(result);
        assertEquals(mockUserRoleEntity.getId(), result.getId());
        assertEquals(mockUserRoleEntity.getName(), result.getName());
        assertEquals(mockUserRoleEntity.getDescription(), result.getDescription());
    }

    @Test
    void givenUserRoleResource_whenMapToEntity_thenReturnUserRoleEntity() {
        // When
        UserRoleEntity result = userRoleMapper.mapToEntity(mockUserRoleResource);

        // Then
        assertNotNull(result);
        assertEquals(mockUserRoleResource.getId(), result.getId());
        assertEquals(mockUserRoleResource.getName(), result.getName());
        assertEquals(mockUserRoleResource.getDescription(), result.getDescription());
    }
}
