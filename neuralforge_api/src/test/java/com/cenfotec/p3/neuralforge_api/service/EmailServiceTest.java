package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private SendGrid sendGrid;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendUserVerificationEmailSuccess() throws NeuralForgeEmailException {
        UserEntity user = new UserEntity();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        doNothing().when(sendGrid).api(any(Request.class));

        assertDoesNotThrow(() -> emailService.sendUserVerificationEmail(user, 123456));
    }

    @Test
    void testSendPasswordResetEmailSuccess() throws NeuralForgeEmailException {
        UserEntity user = new UserEntity();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");

        doNothing().when(sendGrid).api(any(Request.class));

        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(user, "dummyToken"));
    }
}
