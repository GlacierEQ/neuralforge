package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting between SelectedDaysEntity and SelectedDaysResource.
 * Used to map database entities to API resources and vice versa.
 *
 * Author: Jareth Mena
 * Version: 1.0
 */
@Component
public class SelectedDaysMapper {

    /**
     * Converts a SelectedDaysResource into a SelectedDaysEntity.
     *
     * @param resource The resource to convert.
     * @return A new SelectedDaysEntity based on the given resource.
     */
    public SelectedDaysEntity toEntity(SelectedDaysResource resource) {
        return SelectedDaysEntity.builder()
                .monday(resource.isMonday())
                .tuesday(resource.isTuesday())
                .wednesday(resource.isWednesday())
                .thursday(resource.isThursday())
                .friday(resource.isFriday())
                .saturday(resource.isSaturday())
                .sunday(resource.isSunday())
                .build();
    }

    /**
     * Converts a SelectedDaysEntity into a SelectedDaysResource.
     *
     * @param entity The entity to convert.
     * @return A new SelectedDaysResource based on the given entity.
     */
    public SelectedDaysResource toResource(SelectedDaysEntity entity) {
        return SelectedDaysResource.builder()
                .monday(entity.isMonday())
                .tuesday(entity.isTuesday())
                .wednesday(entity.isWednesday())
                .thursday(entity.isThursday())
                .friday(entity.isFriday())
                .saturday(entity.isSaturday())
                .sunday(entity.isSunday())
                .build();
    }
}
