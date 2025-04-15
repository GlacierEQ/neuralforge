package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.PasswordUpdateResource;
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
import org.springframework.web.bind.annotation.DeleteMapping;

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

    /**
     * Deletes the current user's account.
     * This endpoint allows users to delete their own account.
     *
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> deleteCurrentUserAccount() {
        userService.deleteCurrentUser();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    /**
     * Toggles the active status of a user by their ID.
     *
     * This endpoint is restricted to users with the ROLE_ADMINISTRATOR role.
     * It allows administrators to activate or deactivate a user account.
     *
     * @param userId The unique identifier of the user whose status is to be toggled.
     * @return A ResponseEntity with HTTP 200 OK status if the operation is successful.
     */

    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable String userId) {
        userService.toggleUserStatus(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * Updates the authenticated user's password.
     * Verifies the current password before allowing the change and applies password validation.
     * Accessible by users with any role.
     *
     * @param passwordUpdateResource The {@link PasswordUpdateResource} containing current and new passwords.
     * @return A {@link ResponseEntity} with HTTP 204 No Content if the update is successful.
     */
    @PutMapping("/profile/password")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateResource passwordUpdateResource) {
        userService.updatePassword(passwordUpdateResource);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Updates a user's role based on user ID and the new role.
     *
     * @param userId The ID of the user whose role is being updated.
     * @param newRole The new role to assign to the user.
     * @return The updated {@link UserResource} with the new role.
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResource> updateUserRole(@PathVariable String userId, @RequestBody String newRole) {
        return ResponseEntity.ok(userService.updateUserRole(userId, newRole));
    }
}
