package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.service.AuthenticationService;
import com.cenfotec.p3.neuralforge_api.service.UserService;
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
 * Provides endpoints for user login and registration.
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
     */
    @PostMapping("/register")
    public ResponseEntity<UserResource> registerUser(@Valid @RequestBody UserResource user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }
}
