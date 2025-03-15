package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserValidationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserValidationMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.ValidationTypeMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationInputResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;
import com.cenfotec.p3.neuralforge_api.repository.UserValidationRepository;
import com.cenfotec.p3.neuralforge_api.util.GenerationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @InjectMocks
    private UserValidationService userValidationService;

    @Mock
    private UserValidationRepository userValidationRepository;

    @Mock
    private ValidationTypeService validationTypeService;

    @Mock
    private GenerationUtil generationUtil;

    @Mock
    private UserValidationMapper userValidationMapper;

    @Mock
    private ValidationTypeMapper validationTypeMapper;

    private UserEntity mockUserEntity;
    private ValidationTypeResource mockValidationTypeResource;
    private ValidationTypeEntity mockValidationTypeEntity;
    private UserValidationEntity mockUserValidationEntity;
    private UserValidationResource mockUserValidationResource;

    @BeforeEach
    void setUp() {
        mockUserEntity = new UserEntity();
        mockUserEntity.setEmail("test@example.com");

        mockValidationTypeResource = new ValidationTypeResource();
        mockValidationTypeResource.setType(ValidationTypeEnum.VERIFY);

        mockValidationTypeEntity = new ValidationTypeEntity();

        mockUserValidationEntity = new UserValidationEntity();
        mockUserValidationEntity.setUser(mockUserEntity);
        mockUserValidationEntity.setType(mockValidationTypeEntity);
        mockUserValidationEntity.setVerificationCode(123456);
        mockUserValidationEntity.setStatus(true);

        mockUserValidationResource = new UserValidationResource();
    }

    @Test
    void givenValidUser_whenCreateUserValidation_thenReturnUserValidationResource() {
        int generatedCode = 123456;
        when(generationUtil.generateRandomVerificationCode()).thenReturn(generatedCode);
        when(validationTypeService.getValidationTypeByEnum(ValidationTypeEnum.VERIFY))
                .thenReturn(mockValidationTypeResource);
        when(userValidationRepository.save(any(UserValidationEntity.class)))
                .thenReturn(mockUserValidationEntity);

        UserValidationResource result = userValidationService.createUserValidation(mockUserEntity);

        assertNotNull(result);

        ArgumentCaptor<UserValidationEntity> captor = ArgumentCaptor.forClass(UserValidationEntity.class);
        verify(userValidationRepository).save(captor.capture());
        assertEquals(123456, captor.getValue().getVerificationCode());
    }

    @Test
    void givenValidCode_whenValidateInputCode_thenMarkAsValidated() {
        UserValidationInputResource validationInput = new UserValidationInputResource();
        validationInput.setEmail("test@example.com");
        validationInput.setVerificationCode(123456);

        when(userValidationRepository.findLatestPendingValidationByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUserValidationEntity));

        userValidationService.validateInputCode(validationInput);

        assertTrue(mockUserValidationEntity.getStatus());
        verify(userValidationRepository, times(1)).save(mockUserValidationEntity);
    }

    @Test
    void givenInvalidCode_whenValidateInputCode_thenThrowException() {
        UserValidationInputResource validationInput = new UserValidationInputResource();
        validationInput.setEmail("test@example.com");
        validationInput.setVerificationCode(654321);

        when(userValidationRepository.findLatestPendingValidationByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUserValidationEntity));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userValidationService.validateInputCode(validationInput));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Validation code does not match.", exception.getReason());
    }

    @Test
    void givenNoPendingValidations_whenValidateInputCode_thenThrowNotFoundException() {
        UserValidationInputResource validationInput = new UserValidationInputResource();
        validationInput.setEmail("test@example.com");

        when(userValidationRepository.findLatestPendingValidationByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userValidationService.validateInputCode(validationInput));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("This user has no pending validations.", exception.getReason());
    }
}
