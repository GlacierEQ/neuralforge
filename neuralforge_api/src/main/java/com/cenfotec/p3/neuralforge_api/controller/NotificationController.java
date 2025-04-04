package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;
import com.cenfotec.p3.neuralforge_api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user notifications.
 * Provides endpoints for retrieving, creating, updating, and deleting notifications.
 *
 * Base path: /api/notifications
 *
 * @author
 * @version 1.0
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Retrieves all notifications for a given user ID.
     *
     * @param userId The ID of the user.
     * @return A list of {@link NotificationResource}.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public List<NotificationResource> getNotificationsByUser(@PathVariable String userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    /**
     * Retrieves all notifications for a user by their email.
     *
     * @param email The email of the user.
     * @return A list of {@link NotificationResource}.
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public List<NotificationResource> getNotificationsByEmail(@PathVariable String email) {
        return notificationService.getNotificationsByEmail(email);
    }

    /**
     * Creates a new notification for a user.
     *
     * @param resource The {@link NotificationResource} to create.
     * @return The created notification.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public NotificationResource createNotification(@RequestBody NotificationResource resource) {
        return notificationService.createNotification(resource);
    }

    /**
     * Dismisses a notification by its ID.
     *
     * @param notificationId The ID of the notification to dismiss.
     */
    @PutMapping("/{notificationId}/dismiss")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public void dismissNotification(@PathVariable String notificationId) {
        notificationService.dismissNotification(notificationId);
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId The ID of the notification to delete.
     */
    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public void deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
