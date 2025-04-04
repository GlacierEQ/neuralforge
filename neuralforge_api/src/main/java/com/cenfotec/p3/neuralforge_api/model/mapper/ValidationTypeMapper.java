package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;

/**
 * Mapper class responsible for converting between {@link ValidationTypeEntity} and {@link ValidationTypeResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class ValidationTypeMapper {

    /**
     * Converts a {@link ValidationTypeResource} into a {@link ValidationTypeEntity}.
     *
     * @param validationType The {@link ValidationTypeResource} to be mapped.
     * @return A {@link ValidationTypeEntity} containing the mapped data.
     */
    public ValidationTypeEntity mapToEntity(ValidationTypeResource validationType) {
        return ValidationTypeEntity.builder()
                .type(validationType.getType())
                .description(validationType.getDescription())
                .id(validationType.getId())
                .build();
    }

    /**
     * Converts a {@link ValidationTypeEntity} into a {@link ValidationTypeResource}.
     *
     * @param validationType The {@link ValidationTypeEntity} to be mapped.
     * @return A {@link ValidationTypeResource} containing the mapped data.
     */
    public ValidationTypeResource mapToResource(ValidationTypeEntity validationType) {
        return ValidationTypeResource.builder()
                .type(validationType.getType())
                .description(validationType.getDescription())
                .id(validationType.getId())
                .build();
    }
}
