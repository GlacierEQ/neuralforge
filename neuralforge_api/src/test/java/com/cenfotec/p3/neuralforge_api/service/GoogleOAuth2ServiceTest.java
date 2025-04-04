package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.PasswordResetRequestResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2ServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId("123");
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");
    }

    @Test
    void requestPasswordReset_UserExists_SendsEmail() throws NeuralForgeEmailException {
        PasswordResetRequestResource request = new PasswordResetRequestResource();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generatePasswordResetToken(user.getId())).thenReturn("mockToken");

        passwordResetService.requestPasswordReset(request);

        verify(emailService).sendPasswordResetEmail(user, "mockToken");
    }

    @Test
    void requestPasswordReset_UserNotFound_ThrowsException() {
        PasswordResetRequestResource request = new PasswordResetRequestResource();
        request.setEmail("unknown@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> passwordResetService.requestPasswordReset(request));
    }

    @Test
    void resetPassword_ValidToken_UpdatesPassword() {
        String token = "validToken";
        String newPassword = "newSecurePassword";
        when(jwtService.extractClaim(eq(token), any())).thenReturn("123");
        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("hashedPassword");

        passwordResetService.resetPassword(token, newPassword);

        assertEquals("hashedPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_InvalidToken_ThrowsException() {
        String token = "invalidToken";
        when(jwtService.extractClaim(eq(token), any())).thenThrow(new RuntimeException("Invalid or expired token."));

        assertThrows(RuntimeException.class, () -> passwordResetService.resetPassword(token, "newPassword"));
    }

    @Test
    void resetPassword_UserNotFound_ThrowsException() {
        String token = "validToken";
        when(jwtService.extractClaim(eq(token), any())).thenReturn("unknownUser");
        when(userRepository.findById("unknownUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> passwordResetService.resetPassword(token, "newPassword"));
    }
}