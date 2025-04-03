package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;
import lombok.Builder;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting DynamicContentEntity to DynamicContentResource.
 * Exposes methods to map between the entity and resource representations.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
@Component
public class DynamicContentMapper {

    /**
     * Maps a DynamicContentEntity to a DynamicContentResource.
     *
     * @param entity The DynamicContentEntity to be mapped.
     * @return The corresponding DynamicContentResource.
     */
    public DynamicContentResource mapToResource(DynamicContentEntity entity) {
        if (entity == null) {
            return null;
        }

        return DynamicContentResource.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .creationDate(entity.getCreationDate())
                .path(entity.getPath())
                .email(entity.getEmail())
                .type(entity.getType().name())
                .build();
    }

    /**
     * Maps a DynamicContentResource to a DynamicContentEntity.
     *
     * @param resource The DynamicContentResource to be mapped.
     * @return The corresponding DynamicContentEntity.
     */
    public DynamicContentEntity mapToEntity(DynamicContentResource resource) {
        if (resource == null) {
            return null;
        }

        return DynamicContentEntity.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .creationDate(resource.getCreationDate())
                .path(resource.getPath())
                .email(resource.getEmail())
                .type(DynamicContentTypeEnum.valueOf(resource.getType()))
                .build();
    }
}