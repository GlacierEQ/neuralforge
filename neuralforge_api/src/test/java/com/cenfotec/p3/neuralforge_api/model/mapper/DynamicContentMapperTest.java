package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DynamicContentMapperTest {

    @InjectMocks
    private DynamicContentMapper dynamicContentMapper;

    private DynamicContentEntity mockDynamicContentEntity;
    private DynamicContentResource mockDynamicContentResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        LocalDateTime now = LocalDateTime.now();

        mockDynamicContentEntity = new DynamicContentEntity();
        mockDynamicContentEntity.setId("123");
        mockDynamicContentEntity.setTitle("Test Title");
        mockDynamicContentEntity.setPath("src/main/resources/dynamicContent/Test_Title.pdf");
        mockDynamicContentEntity.setEmail("test@example.com");
        mockDynamicContentEntity.setType(DynamicContentTypeEnum.SUMMARY);
        mockDynamicContentEntity.setCreationDate(now);

        mockDynamicContentResource = new DynamicContentResource();
        mockDynamicContentResource.setId("123");
        mockDynamicContentResource.setTitle("Test Title");
        mockDynamicContentResource.setPath("src/main/resources/dynamicContent/Test_Title.pdf");
        mockDynamicContentResource.setEmail("test@example.com");
        mockDynamicContentResource.setType("SUMMARY");
        mockDynamicContentResource.setCreationDate(now);
    }

    @Test
    void givenDynamicContentEntity_whenMapToResource_thenReturnDynamicContentResource() {
        // When
        DynamicContentResource result = dynamicContentMapper.mapToResource(mockDynamicContentEntity);

        // Then
        assertNotNull(result);
        assertEquals(mockDynamicContentEntity.getId(), result.getId());
        assertEquals(mockDynamicContentEntity.getTitle(), result.getTitle());
        assertEquals(mockDynamicContentEntity.getPath(), result.getPath());
        assertEquals(mockDynamicContentEntity.getEmail(), result.getEmail());
        assertEquals(mockDynamicContentEntity.getType().name(), result.getType());
        assertEquals(mockDynamicContentEntity.getCreationDate(), result.getCreationDate());
    }

    @Test
    void givenDynamicContentResource_whenMapToEntity_thenReturnDynamicContentEntity() {
        // When
        DynamicContentEntity result = dynamicContentMapper.mapToEntity(mockDynamicContentResource);

        // Then
        assertNotNull(result);
        assertEquals(mockDynamicContentResource.getId(), result.getId());
        assertEquals(mockDynamicContentResource.getTitle(), result.getTitle());
        assertEquals(mockDynamicContentResource.getPath(), result.getPath());
        assertEquals(mockDynamicContentResource.getEmail(), result.getEmail());
        assertEquals(mockDynamicContentResource.getType(), result.getType().name());
        assertEquals(mockDynamicContentResource.getCreationDate(), result.getCreationDate());
    }
}