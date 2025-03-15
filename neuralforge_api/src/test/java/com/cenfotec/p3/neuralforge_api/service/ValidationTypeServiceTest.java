package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ValidationTypeMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;
import com.cenfotec.p3.neuralforge_api.repository.ValidationTypeRepository;
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
class ValidationTypeServiceTest {

    @InjectMocks
    private ValidationTypeService validationTypeService;

    @Mock
    private ValidationTypeRepository validationTypeRepository;

    @Mock
    private ValidationTypeMapper validationTypeMapper;

    private ValidationTypeEntity mockValidationTypeEntity;
    private ValidationTypeResource mockValidationTypeResource;

    @BeforeEach
    void setUp() {
        mockValidationTypeEntity = new ValidationTypeEntity();
        mockValidationTypeEntity.setType(ValidationTypeEnum.VERIFY);

        mockValidationTypeResource = new ValidationTypeResource();
        mockValidationTypeResource.setType(ValidationTypeEnum.VERIFY);
    }

    @Test
    void givenValidEnum_whenGetValidationTypeByEnum_thenReturnResource() {
        when(validationTypeRepository.findByType(ValidationTypeEnum.VERIFY))
                .thenReturn(Optional.of(mockValidationTypeEntity));

        ValidationTypeResource result = validationTypeService.getValidationTypeByEnum(ValidationTypeEnum.VERIFY);

        assertNotNull(result);
        assertEquals(ValidationTypeEnum.VERIFY, result.getType());
    }

    @Test
    void givenInvalidEnum_whenGetValidationTypeByEnum_thenThrowException() {
        when(validationTypeRepository.findByType(ValidationTypeEnum.RECOVER))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> validationTypeService.getValidationTypeByEnum(ValidationTypeEnum.RECOVER));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Validation type not found", exception.getReason());
    }
}
