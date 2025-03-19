package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller responsible for handling user-related requests.
 * Provides endpoints for retrieving user data.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves a list of all registered users.
     * This endpoint is restricted to users with the 'ROLE_ADMINISTRATOR' role.
     *
     * @return A {@link ResponseEntity} containing a list of {@link UserResource} objects.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<UserResource>> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResource> getUserByEmail(@PathVariable String email) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserByEmail(email));
    }

    @PutMapping("/{email}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResource> updateUserFullAccess(@RequestBody UserResource user, @PathVariable String email) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.handledUserUpdate(email, user));
    }
    
    /**
     * Updates the current user's profile information.
     * This endpoint allows users to update their own profile.
     *
     * @param user The {@link UserResource} containing updated profile information.
     * @return A {@link ResponseEntity} containing the updated {@link UserResource}.
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResource> updateCurrentUserProfile(@RequestBody UserResource user) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateCurrentUserProfile(user));
    }
}
