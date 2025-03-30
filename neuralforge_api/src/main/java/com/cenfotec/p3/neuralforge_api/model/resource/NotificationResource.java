package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.*;

/**
 * Resource (DTO) representing a notification for use in API communication.
 * This class is used to transfer data between the client and server without
 * exposing the full {@link com.cenfotec.p3.neuralforge_api.model.entity.UserEntity}.
 *
 * It replaces the full user object with just the user ID.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResource {

    /**
     * Unique identifier for the notification.
     */
    private String id;

    /**
     * ID of the user associated with this notification.
     */
    private String userId;

    /**
     * Title or heading of the notification.
     */
    private String title;

    /**
     * Detailed message or content of the notification.
     */
    private String description;

    /**
     * Label for the notification's action (e.g., "View", "Open").
     */
    private String actionLabel;

    /**
     * Path or URL where the user should be redirected upon interaction.
     */
    private String redirectTo;

    /**
     * Indicates whether the notification has been dismissed by the user.
     */
    private Boolean dismissed;
}
