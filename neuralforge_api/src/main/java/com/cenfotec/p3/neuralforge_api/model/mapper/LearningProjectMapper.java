package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.LearningProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.LearningProjectResource;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting between {@link LearningProjectEntity} 
 * and {@link LearningProjectResource}.
 * Extends the base {@link ProjectMapper} to provide specific mapping for 
 * learning-focused projects while maintaining the common project mapping logic.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class LearningProjectMapper extends ProjectMapper<LearningProjectEntity, LearningProjectResource> {

    /**
     * Converts a {@link LearningProjectEntity} into a {@link LearningProjectResource}.
     *
     * @param learningProject The {@link LearningProjectEntity} to be mapped.
     * @return A {@link LearningProjectResource} containing the mapped learning project data.
     */
    @Override
    public LearningProjectResource mapToResource(LearningProjectEntity learningProject) {
        return LearningProjectResource.builder()
                .id(learningProject.getId())
                .creatorUserId(learningProject.getCreatorUserId())
                .name(learningProject.getName())
                .createdAt(learningProject.getCreatedAt())
                .description(learningProject.getDescription())
                .projectType(ProjectTypeEnum.LEARNING)
                .build();
    }

    /**
     * Converts a {@link LearningProjectResource} into a {@link LearningProjectEntity}.
     *
     * @param learningProject The {@link LearningProjectResource} to be mapped.
     * @return A {@link LearningProjectEntity} containing the mapped learning project data.
     */
    @Override
    public LearningProjectEntity mapToEntity(LearningProjectResource learningProject) {
        return LearningProjectEntity.builder()
                .id(learningProject.getId())
                .creatorUserId(learningProject.getCreatorUserId())
                .name(learningProject.getName())
                .description(learningProject.getDescription())
                .createdAt(learningProject.getCreatedAt())
                .build();
    }
}