package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

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
    private PasswordEncoder passwordEncoder;

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
    void givenValidUserDetails_whenCreateUser_thenReturnCreatedUser() {
        // Given
        when(userRepository.existsByEmail(mockUserResource.getEmail())).thenReturn(false);
        when(userRoleService.getRoleByEnum(UserRoleEnum.ROLE_STUDENT)).thenReturn(mockRoleResource);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

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
    }



}
