package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationInputResource;
import com.cenfotec.p3.neuralforge_api.service.AuthenticationService;
import com.cenfotec.p3.neuralforge_api.service.UserService;
import com.cenfotec.p3.neuralforge_api.service.UserValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
