package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTypeMapperTest {

    private ValidationTypeMapper validationTypeMapper;

    @BeforeEach
    void setUp() {
        validationTypeMapper = new ValidationTypeMapper();
    }

    @Test
    void givenValidationTypeResource_whenMapToEntity_thenReturnValidationTypeEntity() {
        // Given
        ValidationTypeResource resource = ValidationTypeResource.builder()
                .id("123")
                .type(ValidationTypeEnum.VERIFY)
                .description("Email verification process")
                .build();

        // When
        ValidationTypeEntity entity = validationTypeMapper.mapToEntity(resource);

        // Then
        assertNotNull(entity);
        assertEquals(resource.getId(), entity.getId());
        assertEquals(resource.getType(), entity.getType());
        assertEquals(resource.getDescription(), entity.getDescription());
    }

    @Test
    void givenValidationTypeEntity_whenMapToResource_thenReturnValidationTypeResource() {
        // Given
        ValidationTypeEntity entity = ValidationTypeEntity.builder()
                .id("123")
                .type(ValidationTypeEnum.VERIFY)
                .description("Email verification process")
                .build();

        // When
        ValidationTypeResource resource = validationTypeMapper.mapToResource(entity);

        // Then
        assertNotNull(resource);
        assertEquals(entity.getId(), resource.getId());
        assertEquals(entity.getType(), resource.getType());
        assertEquals(entity.getDescription(), resource.getDescription());
    }
}
