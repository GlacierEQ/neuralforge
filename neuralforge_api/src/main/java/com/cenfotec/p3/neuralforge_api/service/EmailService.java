package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling email-related operations.
 * Utilizes SendGrid for sending verification emails to users.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    protected SendGrid sendGrid;

    /**
     * The sender's email address used for sending emails.
     */
    protected final Email from;

    /**
     * Constructs an instance of {@code EmailService} with the configured sender email.
     *
     * @param senderIdentity The sender email address retrieved from application properties.
     */
    @Autowired
    public EmailService(@Value("${spring.sendgrid.sender-identity}") String senderIdentity) {
        this.from = new Email(senderIdentity);
    }

    /**
     * Sends an email containing a user verification code.
     *
     * @param user The {@link UserEntity} recipient of the verification email.
     * @param verificationCode The verification code to be sent.
     * @throws NeuralForgeEmailException If an error occurs while sending the email.
     */
    public void sendUserVerificationEmail(UserEntity user, int verificationCode) throws NeuralForgeEmailException {
        String subject = "[ NeuralForge Learning ] - Verify your Email";
        Email to = new Email(user.getEmail());
        Content content = new Content(
                "text/plain",
                "Greetings " + user.getName() + ". \n\nThis is your account verification code: " + verificationCode
        );
        Mail mail = new Mail(from, subject, to, content);
        sendEmail(mail);
    }

    /**
     * Sends an email using the SendGrid API.
     *
     * @param mail The {@link Mail} object containing email details.
     * @throws NeuralForgeEmailException If an error occurs while processing the email request.
     */
    protected void sendEmail(Mail mail) throws NeuralForgeEmailException {
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() > 299) {
                throw new NeuralForgeEmailException("A status of " + response.getStatusCode() + " has been returned by the email client. Body: " + response.getBody());
            }
        } catch (Exception ex) {
            throw new NeuralForgeEmailException("An unknown exception has occurred while sending an email: " + ex.getMessage(), ex);
        }
    }
}
