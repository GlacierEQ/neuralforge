package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.LearningProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.LearningProjectResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LearningProjectMapperTest {

    private LearningProjectMapper learningProjectMapper;
    private LearningProjectEntity mockLearningProjectEntity;
    private LearningProjectResource mockLearningProjectResource;

    @BeforeEach
    void setUp() {
        learningProjectMapper = new LearningProjectMapper();

        mockLearningProjectEntity = LearningProjectEntity.builder()
                .id("123")
                .creatorUserId("user456")
                .name("Test Learning Project")
                .description("This is a test learning project")
                .build();
        mockLearningProjectEntity.setProjectType(ProjectTypeEnum.LEARNING);

        mockLearningProjectResource = LearningProjectResource.builder()
                .id("123")
                .creatorUserId("user456")
                .name("Test Learning Project")
                .description("This is a test learning project")
                .projectType(ProjectTypeEnum.LEARNING)
                .build();
    }

    @Test
    void givenLearningProjectEntity_whenMapToResource_thenReturnLearningProjectResource() {
        // When
        LearningProjectResource result = learningProjectMapper.mapToResource(mockLearningProjectEntity);

        // Then
        assertNotNull(result);
        assertEquals(mockLearningProjectEntity.getId(), result.getId());
        assertEquals(mockLearningProjectEntity.getCreatorUserId(), result.getCreatorUserId());
        assertEquals(mockLearningProjectEntity.getName(), result.getName());
        assertEquals(mockLearningProjectEntity.getDescription(), result.getDescription());
        assertEquals(ProjectTypeEnum.LEARNING, result.getProjectType());
    }

    @Test
    void givenLearningProjectResource_whenMapToEntity_thenReturnLearningProjectEntity() {
        // When
        LearningProjectEntity result = learningProjectMapper.mapToEntity(mockLearningProjectResource);

        // Then
        assertNotNull(result);
        assertEquals(mockLearningProjectResource.getId(), result.getId());
        assertEquals(mockLearningProjectResource.getCreatorUserId(), result.getCreatorUserId());
        assertEquals(mockLearningProjectResource.getName(), result.getName());
        assertEquals(mockLearningProjectResource.getDescription(), result.getDescription());
    }
}