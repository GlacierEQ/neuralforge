package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserValidationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidationMapperTest {

    @InjectMocks
    private UserValidationMapper userValidationMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ValidationTypeMapper validationTypeMapper;

    private UserEntity mockUserEntity;
    private ValidationTypeEntity mockValidationTypeEntity;
    private UserValidationEntity mockUserValidationEntity;
    private UserResource mockUserResource;
    private ValidationTypeResource mockValidationTypeResource;
    private UserValidationResource mockUserValidationResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUserEntity = new UserEntity();
        mockValidationTypeEntity = new ValidationTypeEntity();

        mockUserValidationEntity = UserValidationEntity.builder()
                .user(mockUserEntity)
                .type(mockValidationTypeEntity)
                .verificationCode(123456)
                .status(true)
                .requestedAt(LocalDateTime.now())
                .build();

        mockUserResource = new UserResource();
        mockValidationTypeResource = new ValidationTypeResource();
        mockUserValidationResource = UserValidationResource.builder()
                .user(mockUserResource)
                .type(mockValidationTypeResource)
                .verificationCode(123456)
                .status(true)
                .requestedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void givenUserAndValidationType_whenMapToEntity_thenReturnUserValidationEntity() {
        // Given
        int verificationCode = 123456;

        // When
        UserValidationEntity result = userValidationMapper.mapToEntity(mockUserEntity, mockValidationTypeEntity, verificationCode);

        // Then
        assertNotNull(result);
        assertEquals(mockUserEntity, result.getUser());
        assertEquals(mockValidationTypeEntity, result.getType());
        assertEquals(verificationCode, result.getVerificationCode());
    }

    @Test
    void givenUserValidationEntity_whenMapToResource_thenReturnUserValidationResource() {
        // Given
        when(userMapper.mapToResource(mockUserEntity)).thenReturn(mockUserResource);
        when(validationTypeMapper.mapToResource(mockValidationTypeEntity)).thenReturn(mockValidationTypeResource);

        // When
        UserValidationResource result = userValidationMapper.mapToResource(mockUserValidationEntity);

        // Then
        assertNotNull(result);
        assertEquals(mockUserResource, result.getUser());
        assertEquals(mockValidationTypeResource, result.getType());
        assertEquals(mockUserValidationEntity.getVerificationCode(), result.getVerificationCode());
        assertEquals(mockUserValidationEntity.getStatus(), result.getStatus());
        assertEquals(mockUserValidationEntity.getRequestedAt(), result.getRequestedAt());
    }
}