package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationInputResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import com.cenfotec.p3.neuralforge_api.util.ValidationUtil;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleService userRoleService;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ValidationUtil validationUtil;
    @Mock
    private UserValidationService userValidationService;

    @Mock
    private UserMapper userMapper;

    private UserResource mockUserResource;
    private UserEntity mockUserEntity;
    private UserRoleResource mockRoleResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: Mock Role Entity
        UserRoleEntity roleEntity = new UserRoleEntity();
        roleEntity.setName(UserRoleEnum.ROLE_STUDENT);
        roleEntity.setDescription("A student role");

        // Given: Mock Role Resource
        mockRoleResource = new UserRoleResource();
        mockRoleResource.setName(UserRoleEnum.ROLE_STUDENT);

        // Given: Mock User Resource
        mockUserResource = new UserResource();
        mockUserResource.setEmail("test@example.com");
        mockUserResource.setPassword("plainPassword");
        mockUserResource.setRole(mockRoleResource);

        // Given: Mock User Entity with a role
        mockUserEntity = new UserEntity();
        mockUserEntity.setEmail("test@example.com");
        mockUserEntity.setPassword("encodedPassword");
        mockUserEntity.setRole(roleEntity);
    }

    @Test
    void givenExistingEmail_whenCreateUser_thenThrowEntityExistsException() {
        // Given
        when(userRepository.existsByEmail(mockUserResource.getEmail())).thenReturn(true);

        // When & Then
        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> userService.createUser(mockUserResource));

        assertEquals("A user is already registered with this email address.", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(mockUserResource.getEmail());
        verify(userRoleService, never()).getRoleByEnum(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userMapper, never()).mapToEntity(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).mapToResource(any());
    }

    @Test
    void givenUsersExist_whenGetAllUsers_thenReturnUserList() {
        // Given
        List<UserEntity> mockUserEntities = Arrays.asList(mockUserEntity);
        when(userRepository.findAll()).thenReturn(mockUserEntities);

        // When
        List<UserResource> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUserResource.getEmail(), result.get(0).getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenValidUserDetails_whenCreateUser_thenReturnCreatedUser() throws NeuralForgeEmailException {
        // Given
        when(userRepository.existsByEmail(mockUserResource.getEmail())).thenReturn(false);
        when(userRoleService.getRoleByEnum(UserRoleEnum.ROLE_STUDENT)).thenReturn(mockRoleResource);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        UserValidationResource mockValidationResource = new UserValidationResource();
        mockValidationResource.setVerificationCode(12345);

        when(userValidationService.createUserValidation(any(UserEntity.class))).thenReturn(mockValidationResource);
        doNothing().when(emailService).sendUserVerificationEmail(any(UserEntity.class), anyInt());

        // When
        UserResource result = userService.createUser(mockUserResource);

        // Then
        assertNotNull(result);
        assertEquals(mockUserResource.getEmail(), result.getEmail());
        assertEquals(mockRoleResource.getName(), result.getRole().getName());

        verify(userRepository, times(1)).existsByEmail(mockUserResource.getEmail());
        verify(userRoleService, times(1)).getRoleByEnum(UserRoleEnum.ROLE_STUDENT);
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userValidationService, times(1)).createUserValidation(any(UserEntity.class));
        verify(emailService, times(1)).sendUserVerificationEmail(any(UserEntity.class), anyInt());
    }

    @Test
    void givenValidEmail_whenGetUserByEmail_thenReturnUser() {
        // Given
        when(userRepository.findByEmail(mockUserResource.getEmail())).thenReturn(Optional.of(mockUserEntity));

        // When
        UserResource result = userService.getUserByEmail(mockUserResource.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(mockUserResource.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(mockUserResource.getEmail());
    }


    @Test
    void givenNonExistentEmail_whenGetUserByEmail_thenThrowException() {
        // Given
        when(userRepository.findByEmail(mockUserResource.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.getUserByEmail(mockUserResource.getEmail()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, times(1)).findByEmail(mockUserResource.getEmail());
    }

    @Test
    void givenValidUserDetails_whenHandledUserUpdate_thenReturnUpdatedUser() {
        // Given
        when(userRepository.existsByEmail(mockUserResource.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(mockUserResource.getEmail())).thenReturn(Optional.of(mockUserEntity));

        doNothing().when(validationUtil).triggerValidations(any(), any());

        // When
        UserResource result = userService.handledUserUpdate(mockUserResource.getEmail(), mockUserResource);

        // Then
        assertNotNull(result);
        assertEquals(mockUserResource.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsByEmail(mockUserResource.getEmail());
        verify(userRepository, times(1)).updateUserIgnoringNulls(any(), any(), any(), any(), any(), any(), any());
        verify(validationUtil, times(1)).triggerValidations(any(), eq("password"));
    }

    @Test
    void givenInvalidEmail_whenHandledUserUpdate_thenThrowException() {
        // Given
        when(userRepository.existsByEmail(mockUserResource.getEmail())).thenReturn(false);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.handledUserUpdate(mockUserResource.getEmail(), mockUserResource));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, times(1)).existsByEmail(mockUserResource.getEmail());
    }

    @Test
    void givenValidValidationInput_whenValidateInitialRegister_thenUpdateUserVerification() {
        // Given
        UserValidationInputResource validationInput = new UserValidationInputResource();
        validationInput.setEmail(mockUserResource.getEmail());
        validationInput.setVerificationCode(12345);

        when(userRepository.findByEmail(mockUserResource.getEmail())).thenReturn(Optional.of(mockUserEntity));
        doNothing().when(userValidationService).validateInputCode(validationInput);
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        // When
        userService.validateInitialRegister(validationInput);

        // Then
        verify(userValidationService, times(1)).validateInputCode(validationInput);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void whenGetCurrentUser_thenReturnAuthenticatedUserDetails() {
        // Given
        String authenticatedEmail = "test@example.com";
        
        // Mock SecurityContext and Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedEmail);
        SecurityContextHolder.setContext(securityContext);
        
        // Create a new UserService with a real UserMapper (not mocked)
        UserService realUserService = new UserService();
        ReflectionTestUtils.setField(realUserService, "userRepository", userRepository);
        
        // Mock repository response
        when(userRepository.findByEmail(authenticatedEmail))
            .thenReturn(Optional.of(mockUserEntity));

        // When
        UserResource result = realUserService.getCurrentUser();

        // Then
        assertNotNull(result);
        assertEquals(authenticatedEmail, result.getEmail());
        verify(userRepository).findByEmail(authenticatedEmail);
        
        // No need to verify userMapper.mapToResource as we're using a real mapper
        
        // Clean up security context
        SecurityContextHolder.clearContext();
    }

}
