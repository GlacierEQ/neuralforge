package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgrammedGoalProjectMapperTest {

    private ProgrammedGoalProjectMapper mapper;
    private SelectedDaysMapper selectedDaysMapper;
    private DynamicContentMapper dynamicContentMapper;

    @BeforeEach
    void setUp() {
        selectedDaysMapper = mock(SelectedDaysMapper.class);
        dynamicContentMapper = mock(DynamicContentMapper.class);

        mapper = new ProgrammedGoalProjectMapper();
        mapper.selectedDaysMapper = selectedDaysMapper;
        mapper.dynamicContentMapper = dynamicContentMapper;
    }

    @Test
    void whenMapToResource_thenReturnCorrectResource() {
        ProgrammedGoalProjectEntity entity = ProgrammedGoalProjectEntity.builder()
                .id("id123")
                .creatorUserId("user123")
                .name("Test Project")
                .description("Test Description")
                .deadline(new Date())
                .createdAt(new Date())
                .notify(true)
                .selectedDays(new SelectedDaysEntity())
                .dynamicContents(Collections.emptyList())
                .build();

        when(selectedDaysMapper.toResource(any())).thenReturn(new SelectedDaysResource());

        var result = mapper.mapToResource(entity);

        assertNotNull(result);
        assertEquals("id123", result.getId());
        assertEquals("user123", result.getCreatorUserId());
        assertTrue(result.getNotify());
        verify(selectedDaysMapper, times(1)).toResource(any());
    }

    @Test
    void whenMapToEntity_thenReturnCorrectEntity() {
        ProgrammedGoalProjectResource resource = ProgrammedGoalProjectResource.builder()
                .id("id123")
                .creatorUserId("user123")
                .name("Test Project")
                .description("Test Description")
                .deadline(new Date())
                .createdAt(new Date())
                .notify(true)
                .selectedDays(new SelectedDaysResource())
                .dynamicContents(Collections.emptyList())
                .build();

        when(selectedDaysMapper.toEntity(any())).thenReturn(new SelectedDaysEntity());

        var result = mapper.mapToEntity(resource);

        assertNotNull(result);
        assertEquals("id123", result.getId());
        assertEquals("user123", result.getCreatorUserId());
        assertTrue(result.getNotify());
        verify(selectedDaysMapper, times(1)).toEntity(any());
    }
}
