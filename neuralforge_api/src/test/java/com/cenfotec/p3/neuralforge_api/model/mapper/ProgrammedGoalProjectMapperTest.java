package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgrammedGoalProjectMapperTest {

    private ProgrammedGoalProjectMapper mapper;
    private SelectedDaysMapper selectedDaysMapper;
    private DynamicContentMapper dynamicContentMapper;
    private ProjectMaterialMapper projectMaterialMapper;

    @BeforeEach
    void setUp() {
        selectedDaysMapper = mock(SelectedDaysMapper.class);
        dynamicContentMapper = mock(DynamicContentMapper.class);
        projectMaterialMapper = mock(ProjectMaterialMapper.class);

        mapper = new ProgrammedGoalProjectMapper();
        mapper.selectedDaysMapper = selectedDaysMapper;
        mapper.dynamicContentMapper = dynamicContentMapper;
        mapper.projectMaterialMapper = projectMaterialMapper;
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
                .materials(new ArrayList<>())
                .build();

        when(selectedDaysMapper.toResource(any())).thenReturn(new SelectedDaysResource());
        when(projectMaterialMapper.mapToResource(any())).thenReturn(new ProjectMaterialResource());

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
                .materials(new ArrayList<>())
                .build();

        when(selectedDaysMapper.toEntity(any())).thenReturn(new SelectedDaysEntity());
        when(projectMaterialMapper.mapToEntity(any())).thenReturn(new ProjectMaterialEntity());

        var result = mapper.mapToEntity(resource);

        assertNotNull(result);
        assertEquals("id123", result.getId());
        assertEquals("user123", result.getCreatorUserId());
        assertTrue(result.getNotify());
        verify(selectedDaysMapper, times(1)).toEntity(any());
    }
}
