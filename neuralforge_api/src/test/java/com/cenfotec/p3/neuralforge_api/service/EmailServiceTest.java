package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private SendGrid sendGrid;

    @Mock
    private Response mockResponse;

    @Value("${spring.sendgrid.sender-identity}")
    private String senderIdentity = "test@example.com";

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserEntity();
        testUser.setEmail("user@example.com");
        testUser.setName("Test User");
    }

    @Test
    void givenValidUserAndCode_whenSendUserVerificationEmail_thenNoExceptionThrown() throws Exception {
        // Given
        int verificationCode = 123456;
        Request request = new Request();
        when(sendGrid.api(any(Request.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusCode()).thenReturn(200);

        // When & Then
        assertDoesNotThrow(() -> emailService.sendUserVerificationEmail(testUser, verificationCode));
        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    void givenSendGridFailure_whenSendUserVerificationEmail_thenThrowNeuralForgeEmailException() throws Exception {
        // Given
        int verificationCode = 123456;
        Request request = new Request();
        when(sendGrid.api(any(Request.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusCode()).thenReturn(500);
        when(mockResponse.getBody()).thenReturn("Internal Server Error");

        // When & Then
        NeuralForgeEmailException exception = assertThrows(NeuralForgeEmailException.class, () ->
                emailService.sendUserVerificationEmail(testUser, verificationCode));
        assertTrue(exception.getMessage().contains("500"));
        assertTrue(exception.getMessage().contains("Internal Server Error"));
    }

    @Test
    void givenSendGridThrowsException_whenSendUserVerificationEmail_thenThrowNeuralForgeEmailException() throws Exception {
        // Given
        int verificationCode = 123456;
        when(sendGrid.api(any(Request.class))).thenThrow(new RuntimeException("Connection error"));

        // When & Then
        NeuralForgeEmailException exception = assertThrows(NeuralForgeEmailException.class, () ->
                emailService.sendUserVerificationEmail(testUser, verificationCode));
        assertTrue(exception.getMessage().contains("An unknown exception has occurred while sending an email"));
    }
}
