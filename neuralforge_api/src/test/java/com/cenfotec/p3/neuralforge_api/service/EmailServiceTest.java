package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.sendgrid.helpers.mail.Mail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        emailService = new TestEmailService("test@example.com");
        testUser = new UserEntity();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
    }

    @Test
    void sendUserVerificationEmail_Success() {
        assertDoesNotThrow(() -> emailService.sendUserVerificationEmail(testUser, 123456));
    }

    @Test
    void sendPasswordResetEmail_Success() {
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(testUser, "fakeToken"));
    }

    private static class TestEmailService extends EmailService {
        public TestEmailService(String senderIdentity) {
            super(senderIdentity);
        }

        @Override
        protected void sendEmail(Mail mail) {

        }
    }
}