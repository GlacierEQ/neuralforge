package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectedDaysMapperTest {

    private SelectedDaysMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SelectedDaysMapper();
    }

    @Test
    void whenToEntity_thenFieldsMatch() {
        SelectedDaysResource resource = SelectedDaysResource.builder()
                .monday(true)
                .tuesday(false)
                .wednesday(true)
                .thursday(false)
                .friday(true)
                .saturday(false)
                .sunday(true)
                .build();

        SelectedDaysEntity entity = mapper.toEntity(resource);

        assertTrue(entity.isMonday());
        assertFalse(entity.isTuesday());
        assertTrue(entity.isWednesday());
        assertFalse(entity.isThursday());
        assertTrue(entity.isFriday());
        assertFalse(entity.isSaturday());
        assertTrue(entity.isSunday());
    }

    @Test
    void whenToResource_thenFieldsMatch() {
        SelectedDaysEntity entity = SelectedDaysEntity.builder()
                .monday(false)
                .tuesday(true)
                .wednesday(false)
                .thursday(true)
                .friday(false)
                .saturday(true)
                .sunday(false)
                .build();

        SelectedDaysResource resource = mapper.toResource(entity);

        assertFalse(resource.isMonday());
        assertTrue(resource.isTuesday());
        assertFalse(resource.isWednesday());
        assertTrue(resource.isThursday());
        assertFalse(resource.isFriday());
        assertTrue(resource.isSaturday());
        assertFalse(resource.isSunday());
    }
}
