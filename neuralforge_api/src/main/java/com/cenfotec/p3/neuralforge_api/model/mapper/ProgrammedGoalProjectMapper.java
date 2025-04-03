package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting between {@link ProgrammedGoalProjectEntity}
 * and {@link ProgrammedGoalProjectResource}.
 * Extends the base {@link ProjectMapper} to provide specific mapping for
 * learning-focused projects while maintaining the common project mapping logic.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class ProgrammedGoalProjectMapper extends ProjectMapper<ProgrammedGoalProjectEntity, ProgrammedGoalProjectResource> {

    @Autowired
    protected SelectedDaysMapper selectedDaysMapper;
    @Autowired
    protected DynamicContentMapper dynamicContentMapper;

    /**
     * Maps a {@link ProgrammedGoalProjectEntity} to its corresponding
     * {@link ProgrammedGoalProjectResource}.
     *
     * @param entity The ProgrammedGoalProjectEntity to be mapped.
     * @return A ProgrammedGoalProjectResource containing the mapped data.
     */
    @Override
    public ProgrammedGoalProjectResource mapToResource(ProgrammedGoalProjectEntity entity) {
        return ProgrammedGoalProjectResource.builder()
                .id(entity.getId())
                .creatorUserId(entity.getCreatorUserId())
                .name(entity.getName())
                .description(entity.getDescription())
                .deadline(entity.getDeadline())
                .createdAt(entity.getCreatedAt())
                .notify(entity.getNotify())
                .projectType(ProjectTypeEnum.PROGRAMMED_GOAL)
                .selectedDays(selectedDaysMapper.toResource(entity.getSelectedDays()))
                .dynamicContents(entity.getDynamicContents() != null
                        ? entity.getDynamicContents().stream().map(dynamicContentMapper::mapToResource).toList()
                        : null)
                .build();
    }

    /**
     * Maps a {@link ProgrammedGoalProjectResource} to its corresponding
     * {@link ProgrammedGoalProjectEntity}.
     *
     * @param resource The ProgrammedGoalProjectResource to be mapped.
     * @return A ProgrammedGoalProjectEntity containing the mapped data.
     */
    @Override
    public ProgrammedGoalProjectEntity mapToEntity(ProgrammedGoalProjectResource resource) {
        return ProgrammedGoalProjectEntity.builder()
                .id(resource.getId())
                .creatorUserId(resource.getCreatorUserId())
                .name(resource.getName())
                .description(resource.getDescription())
                .deadline(resource.getDeadline())
                .notify(resource.getNotify())
                .createdAt(resource.getCreatedAt())
                .selectedDays(selectedDaysMapper.toEntity(resource.getSelectedDays()))
                .dynamicContents(resource.getDynamicContents() != null
                        ? resource.getDynamicContents().stream().map(dynamicContentMapper::mapToEntity).toList()
                        : null)
                .build();
    }
}


