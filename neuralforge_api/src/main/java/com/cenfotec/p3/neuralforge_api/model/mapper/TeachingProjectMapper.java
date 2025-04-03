package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting between {@link TeachingProjectEntity} 
 * and {@link TeachingProjectResource}.
 * Extends the base {@link ProjectMapper} to provide specific mapping for 
 * teaching-focused projects while maintaining the common project mapping logic.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class TeachingProjectMapper extends ProjectMapper<TeachingProjectEntity, TeachingProjectResource> {

    private final ProjectMaterialMapper projectMaterialMapper;
    private final SelectedDaysMapper selectedDaysMapper;

    public TeachingProjectMapper(ProjectMaterialMapper projectMaterialMapper, SelectedDaysMapper selectedDaysMapper) {
        super();
        this.projectMaterialMapper = projectMaterialMapper;
        this.selectedDaysMapper = selectedDaysMapper;
    }

    /**
     * Converts a {@link TeachingProjectEntity} into a {@link TeachingProjectResource}.
     *
     * @param teachingProject The {@link TeachingProjectEntity} to be mapped.
     * @return A {@link TeachingProjectResource} containing the mapped teaching project data.
     */
    @Override
    public TeachingProjectResource mapToResource(TeachingProjectEntity teachingProject) {
        return TeachingProjectResource.builder()
                .id(teachingProject.getId())
                .creatorUserId(teachingProject.getCreatorUserId())
                .name(teachingProject.getName())
                .description(teachingProject.getDescription())
                .createdAt(teachingProject.getCreatedAt())
                .projectType(ProjectTypeEnum.TEACHING)
                .materials(teachingProject.getMaterials() != null 
                    ? teachingProject.getMaterials().stream().map(projectMaterialMapper::mapToResource).toList()
                    : null)
                .selectedDays(selectedDaysMapper.toResource(teachingProject.getSelectedDays()))
                .dailyHours(teachingProject.getDailyHours())
                .weeksCount(teachingProject.getWeeksCount())
                .startDate(teachingProject.getStartDate())
                .endDate(teachingProject.getEndDate())
                .build();
    }

    /**
     * Converts a {@link TeachingProjectResource} into a {@link TeachingProjectEntity}.
     *
     * @param teachingProject The {@link TeachingProjectResource} to be mapped.
     * @return A {@link TeachingProjectEntity} containing the mapped teaching project data.
     */
    @Override
    public TeachingProjectEntity mapToEntity(TeachingProjectResource teachingProject) {
        return TeachingProjectEntity.builder()
                .id(teachingProject.getId())
                .creatorUserId(teachingProject.getCreatorUserId())
                .name(teachingProject.getName())
                .description(teachingProject.getDescription())
                .createdAt(teachingProject.getCreatedAt())
                .materials(teachingProject.getMaterials() != null 
                    ? teachingProject.getMaterials().stream().map(projectMaterialMapper::mapToEntity).toList()
                    : null)
                .selectedDays(selectedDaysMapper.toEntity(teachingProject.getSelectedDays()))
                .dailyHours(teachingProject.getDailyHours())
                .weeksCount(teachingProject.getWeeksCount())
                .startDate(teachingProject.getStartDate())
                .endDate(teachingProject.getEndDate())
                .build();
    }
} 