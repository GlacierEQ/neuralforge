package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.NotificationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.NotificationMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;
import com.cenfotec.p3.neuralforge_api.repository.NotificationRepository;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for managing notification logic, including creation, retrieval, update, and deletion.
 * Handles user lookups and maps between entity and resource models.
 *
 * @author
 * @version 1.0
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private final NotificationMapper notificationMapper = new NotificationMapper();

    /**
     * Retrieves all notifications for a given user.
     *
     * @param userId The ID of the user.
     * @return A list of {@link NotificationResource} for the user.
     */
    public List<NotificationResource> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(notificationMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves notifications for a user using their email.
     *
     * @param email The email of the user.
     * @return A list of {@link NotificationResource}.
     */
    public List<NotificationResource> getNotificationsByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return getNotificationsForUser(user.getId());
    }

    /**
     * Creates a new notification for a user and sends them an email alert.
     *
     * @param resource The {@link NotificationResource} to save.
     * @return The saved {@link NotificationResource}.
     */
    public NotificationResource createNotification(NotificationResource resource) {
        UserEntity user = userRepository.findById(resource.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        NotificationEntity entity = notificationMapper.mapToEntity(resource, user);
        NotificationEntity saved = notificationRepository.save(entity);

        try {
            emailService.sendNotificationAlertEmail(user, saved.getTitle(), saved.getRedirectTo(), saved.getDescription());
        } catch (NeuralForgeEmailException e) {
            System.err.println("Failed to send notification email: " + e.getMessage());
        }

        return notificationMapper.mapToResource(saved);
    }


    /**
     * Dismisses a notification by setting its dismissed flag to true.
     *
     * @param notificationId The ID of the notification to dismiss.
     */
    public void dismissNotification(String notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setDismissed(true);
        notificationRepository.save(notification);
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId The ID of the notification to delete.
     */
    public void deleteNotification(String notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        notificationRepository.deleteById(notificationId);
    }
}
