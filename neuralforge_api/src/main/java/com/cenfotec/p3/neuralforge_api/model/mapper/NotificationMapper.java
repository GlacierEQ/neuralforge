package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.NotificationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;

/**
 * Mapper class responsible for converting between {@link NotificationEntity} and {@link NotificationResource}.
 * Used to ensure clean transformation between database entities and API-level data structures.
 *
 * Replaces full {@link UserEntity} with a String user ID in the resource.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class NotificationMapper {

    /**
     * Converts a {@link NotificationEntity} into a {@link NotificationResource}.
     *
     * @param notification The {@link NotificationEntity} to map.
     * @return A {@link NotificationResource} with simplified user ID reference.
     */
    public NotificationResource mapToResource(NotificationEntity notification) {
        return NotificationResource.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .actionLabel(notification.getActionLabel())
                .redirectTo(notification.getRedirectTo())
                .dismissed(notification.getDismissed())
                .build();
    }

    /**
     * Converts a {@link NotificationResource} into a {@link NotificationEntity}.
     *
     * Note: Only sets the user ID reference. You must load the full {@link UserEntity}
     * elsewhere (e.g., via service or repository) before persisting.
     *
     * @param resource The {@link NotificationResource} to map.
     * @param user The {@link UserEntity} associated with this notification.
     * @return A {@link NotificationEntity} ready for persistence.
     */
    public NotificationEntity mapToEntity(NotificationResource resource, UserEntity user) {
        return NotificationEntity.builder()
                .id(resource.getId())
                .user(user)
                .title(resource.getTitle())
                .description(resource.getDescription())
                .actionLabel(resource.getActionLabel())
                .redirectTo(resource.getRedirectTo())
                .dismissed(resource.getDismissed())
                .build();
    }
}
