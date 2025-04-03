package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting between {@link ProjectMaterialEntity} 
 * and {@link ProjectMaterialResource}.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class ProjectMaterialMapper {

    /**
     * Converts a {@link ProjectMaterialEntity} into a {@link ProjectMaterialResource}.
     *
     * @param material The {@link ProjectMaterialEntity} to be mapped.
     * @return A {@link ProjectMaterialResource} containing the mapped material data.
     */
    public ProjectMaterialResource mapToResource(ProjectMaterialEntity material) {
        return ProjectMaterialResource.builder()
                .id(material.getId())
                .type(material.getType())
                .fileName(material.getFileName())
                .fileUrl(material.getFileUrl())
                .description(material.getDescription())
                .hyperlink(material.getHyperlink())
                .build();
    }

    /**
     * Converts a {@link ProjectMaterialResource} into a {@link ProjectMaterialEntity}.
     *
     * @param material The {@link ProjectMaterialResource} to be mapped.
     * @return A {@link ProjectMaterialEntity} containing the mapped material data.
     */
    public ProjectMaterialEntity mapToEntity(ProjectMaterialResource material) {
        return ProjectMaterialEntity.builder()
                .id(material.getId())
                .type(material.getType())
                .fileName(material.getFileName())
                .fileUrl(material.getFileUrl())
                .description(material.getDescription())
                .hyperlink(material.getHyperlink())
                .build();
    }
} 