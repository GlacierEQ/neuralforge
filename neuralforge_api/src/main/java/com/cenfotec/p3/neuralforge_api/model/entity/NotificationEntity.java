package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a notification in the system.
 * Each notification is associated with a specific user and contains
 * information about events or actions the user should be aware of.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Table(name = "notifications")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    /**
     * Unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * The user associated with this notification.
     * This establishes a many-to-one relationship with the UserEntity.
     * Cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    /**
     * The title or heading of the notification.
     */
    private String title;

    /**
     * Detailed description or message of the notification.
     */
    private String description;

    /**
     * Label for the action button or link shown with the notification.
     */
    private String actionLabel;

    /**
     * URL or path to redirect the user when the action is performed.
     */
    private String redirectTo;

    /**
     * Flag indicating whether the notification has been dismissed by the user.
     */
    private Boolean dismissed;
}
