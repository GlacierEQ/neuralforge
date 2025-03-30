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
 * Utilizes SendGrid for sending verification and password reset emails.
 *
 * @author Jareth Mena
 * @version 1.1
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
     * Sends an email containing a password reset link.
     *
     * @param user The {@link UserEntity} recipient of the password reset email.
     * @param token The JWT token used to authenticate the password reset request.
     * @throws NeuralForgeEmailException If an error occurs while sending the email.
     */
    public void sendPasswordResetEmail(UserEntity user, String token) throws NeuralForgeEmailException {
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String body = "Hello " + user.getName() + ",\n\n"
                + "You have requested to reset your password. Please click the link below to reset it:\n\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, please ignore this email.";

        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", body);
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
                throw new NeuralForgeEmailException("Email sending failed with status code: "
                        + response.getStatusCode() + ". Response body: " + response.getBody());
            }
        } catch (Exception ex) {
            throw new NeuralForgeEmailException("An error occurred while sending an email: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sends an email to notify the user about a new in-app notification.
     *
     * @param user The {@link UserEntity} recipient of the email.
     * @param notificationTitle The title of the notification received.
     * @param redirectTo The redirect path (e.g. "/profile") to build the full URL.
     * @throws NeuralForgeEmailException If sending the email fails.
     */
    public void sendNotificationAlertEmail(UserEntity user, String notificationTitle, String redirectTo, String notificationDescription) throws NeuralForgeEmailException {
        String subject = "[ NeuralForge ] - "+notificationTitle;
        String redirectUrl = "http://localhost:4200" + (redirectTo != null ? redirectTo : "");

        String body = "Hello " + user.getName() + ",\n\n"
                + "You’ve received a new notification:\n"
                + "\"" + notificationDescription + "\"\n\n"
                + "You can view it here:\n" + redirectUrl + "\n\n"
                + "— NeuralForge Team";

        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);
        sendEmail(mail);
    }

}