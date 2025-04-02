package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserEntity testUser;
    private UserResource testUserResource;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setVerified(true);
        testUser.setStatus(true);

        UserRoleEntity role = new UserRoleEntity();
        role.setDescription("ROLE_USER");
        testUser.setRole(role);

        testUserResource = new UserResource();
        testUserResource.setEmail("test@example.com");
        testUserResource.setPassword("password123");
    }

    @Test
    void givenValidCredentials_whenAuthenticate_thenReturnAuthToken() {
        // Given
        when(userRepository.findByEmail(testUserResource.getEmail())).thenReturn(Optional.of(testUser));
        doAnswer(invocation -> null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("mockedToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        // When
        AuthenticationResource result = authenticationService.authenticate(testUserResource);

        // Then
        assertNotNull(result);
        assertEquals("mockedToken", result.getToken());
        assertEquals(3600L, result.getExpiresIn());

        verify(userRepository, times(1)).findByEmail(testUserResource.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(any(UserEntity.class));
        verify(jwtService, times(1)).getExpirationTime();
    }

    @Test
    void givenNonExistentUser_whenAuthenticate_thenThrowNotFoundException() {
        // Given
        when(userRepository.findByEmail(testUserResource.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationService.authenticate(testUserResource);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());

        verify(userRepository, times(1)).findByEmail(testUserResource.getEmail());
        verifyNoInteractions(authenticationManager, jwtService, userMapper);
    }

    @Test
    void givenInvalidCredentials_whenAuthenticate_thenThrowUnauthorizedException() {
        // Given
        when(userRepository.findByEmail(testUserResource.getEmail())).thenReturn(Optional.of(testUser));
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationService.authenticate(testUserResource);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());

        verify(userRepository, times(1)).findByEmail(testUserResource.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService, userMapper);
    }
}
