package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import com.cenfotec.p3.neuralforge_api.repository.SelectedDaysRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectedDaysServiceTest {

    @InjectMocks
    private SelectedDaysService service;

    @Mock
    private SelectedDaysRepository repository;

    private SelectedDaysResource resource;
    private SelectedDaysEntity entity;

    @BeforeEach
    void setUp() {
        resource = SelectedDaysResource.builder()
                .monday(true)
                .tuesday(false)
                .wednesday(true)
                .thursday(false)
                .friday(true)
                .saturday(false)
                .sunday(true)
                .build();

        entity = SelectedDaysEntity.builder()
                .id("days123")
                .monday(true)
                .tuesday(false)
                .wednesday(true)
                .thursday(false)
                .friday(true)
                .saturday(false)
                .sunday(true)
                .build();
    }

    @Test
    void whenSave_thenReturnSavedEntity() {
        when(repository.save(any())).thenReturn(entity);

        SelectedDaysEntity result = service.save(resource);

        assertNotNull(result);
        assertTrue(result.isMonday());
        verify(repository, times(1)).save(any());
    }

    @Test
    void whenGetById_thenReturnEntity() {
        when(repository.findById("days123")).thenReturn(Optional.of(entity));

        SelectedDaysEntity result = service.getById("days123");

        assertNotNull(result);
        assertEquals("days123", result.getId());
        verify(repository, times(1)).findById("days123");
    }

    @Test
    void whenGetByInvalidId_thenThrow() {
        when(repository.findById("invalid"))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getById("invalid"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenUpdate_thenReturnUpdatedEntity() {
        when(repository.findById("days123")).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        SelectedDaysEntity result = service.update("days123", resource);

        assertNotNull(result);
        assertTrue(result.isMonday());
        verify(repository, times(1)).save(any());
    }

    @Test
    void whenUpdateInvalidId_thenThrow() {
        when(repository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.update("invalid", resource));
    }
}
