package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.resource.*;
import com.cenfotec.p3.neuralforge_api.service.AuthenticationService;
import com.cenfotec.p3.neuralforge_api.service.UserService;
import com.cenfotec.p3.neuralforge_api.service.UserValidationService;
import com.cenfotec.p3.neuralforge_api.service.PasswordResetService;
import com.cenfotec.p3.neuralforge_api.service.GoogleOAuth2Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling authentication-related requests.
 * Provides endpoints for user login, registration, and validation.
 * 
 * @author Jareth Mena
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private GoogleOAuth2Service googleOAuth2Service;

    /**
     * Handles user login requests.
     * Authenticates the user and returns a JWT token upon successful authentication.
     * 
     * @param user The {@link UserResource} containing user credentials.
     * @return A {@link ResponseEntity} containing an {@link AuthenticationResource} with the authentication token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResource> loginUser(@RequestBody UserResource user) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationService.authenticate(user));
    }

    /**
     * Handles user registration requests.
     * Creates a new user account and returns the registered user details.
     * 
     * @param user The {@link UserResource} containing user information.
     * @return A {@link ResponseEntity} containing the newly created {@link UserResource}.
     * @throws NeuralForgeEmailException If there is an issue with the email address provided during registration.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResource> registerUser(@Valid @RequestBody UserResource user) throws NeuralForgeEmailException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    /**
     * Handles user validation requests during initial registration.
     * Validates user-provided data before completing the registration process.
     * 
     * @param validationInput The {@link UserValidationInputResource} containing validation data.
     * @return A {@link ResponseEntity} with HTTP status 200 (OK) upon successful validation.
     */
    @PostMapping("/verify")
    public ResponseEntity<Void> validateInitialRegister(@Valid @RequestBody UserValidationInputResource validationInput) {
        userService.validateInitialRegister(validationInput);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * Handles password reset requests.
     * Sends a password reset email with a link to reset the password.
     *
     * @param request The {@link PasswordResetRequestResource} containing the user's email.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequestResource request) {
        try {
            passwordResetService.requestPasswordReset(request);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (NeuralForgeEmailException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending the password reset email.");
        }
    }

    /**
     * Endpoint for resetting the password.
     * Receives the token and the new password to reset the user's password.
     *
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetResource request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * Handles authentication via Google OAuth.
     * Receives an authentication token from Google and validates the user.
     *
     * @param token The authentication token provided by Google OAuth.
     * @return A {@link ResponseEntity} containing an {@link AuthenticationResource} with the authentication details.
     */
    @PostMapping("/google-auth")
    public ResponseEntity<AuthenticationResource> authenticateWithGoogle(@RequestBody String token) {
        AuthenticationResource response = googleOAuth2Service.authenticate(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the currently authenticated user's profile information.
     * Returns details including first name, last name, email, registration date,
     * and last password change date.
     *
     * @return A {@link ResponseEntity} containing the {@link UserResource} with current user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResource> getCurrentUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getCurrentUser());
    }
}