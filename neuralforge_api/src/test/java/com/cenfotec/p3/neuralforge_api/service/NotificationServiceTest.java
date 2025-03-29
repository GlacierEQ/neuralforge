package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.NotificationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.NotificationMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;
import com.cenfotec.p3.neuralforge_api.repository.NotificationRepository;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Spy
    private NotificationMapper notificationMapper = new NotificationMapper();

    private UserEntity mockUser;
    private NotificationEntity mockEntity;
    private NotificationResource mockResource;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id("user-123")
                .name("Test")
                .email("test@example.com")
                .build();

        mockEntity = NotificationEntity.builder()
                .id("notif-123")
                .user(mockUser)
                .title("Test Notification")
                .description("This is a test")
                .actionLabel("Open")
                .redirectTo("/dashboard")
                .dismissed(false)
                .build();

        mockResource = NotificationResource.builder()
                .id("notif-123")
                .userId("user-123")
                .title("Test Notification")
                .description("This is a test")
                .actionLabel("Open")
                .redirectTo("/dashboard")
                .dismissed(false)
                .build();
    }

    @Test
    void testCreateNotification_success() throws NeuralForgeEmailException {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(mockUser));
        when(notificationRepository.save(any())).thenReturn(mockEntity);

        NotificationResource result = notificationService.createNotification(mockResource);

        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());

        verify(notificationRepository).save(any(NotificationEntity.class));
        verify(emailService).sendNotificationAlertEmail(
                mockUser, mockEntity.getTitle(), mockEntity.getRedirectTo(), mockEntity.getDescription()
        );
    }

    @Test
    void testCreateNotification_userNotFound_shouldThrow() {
        when(userRepository.findById("user-123")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationService.createNotification(mockResource);
        });

        assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage());
        verifyNoInteractions(notificationRepository);
        verifyNoInteractions(emailService);
    }

    @Test
    void testGetNotificationsForUser() {
        when(notificationRepository.findByUserId("user-123")).thenReturn(List.of(mockEntity));

        List<NotificationResource> result = notificationService.getNotificationsForUser("user-123");

        assertEquals(1, result.size());
        assertEquals("Test Notification", result.get(0).getTitle());
    }

    @Test
    void testGetNotificationsByEmail_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(notificationRepository.findByUserId("user-123")).thenReturn(List.of(mockEntity));

        List<NotificationResource> result = notificationService.getNotificationsByEmail("test@example.com");

        assertEquals(1, result.size());
        assertEquals("Test Notification", result.get(0).getTitle());
    }

    @Test
    void testGetNotificationsByEmail_userNotFound_shouldThrow() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationService.getNotificationsByEmail("notfound@example.com");
        });

        assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage());
    }

    @Test
    void testDismissNotification_success() {
        when(notificationRepository.findById("notif-123")).thenReturn(Optional.of(mockEntity));

        notificationService.dismissNotification("notif-123");

        assertTrue(mockEntity.getDismissed());
        verify(notificationRepository).save(mockEntity);
    }

    @Test
    void testDismissNotification_notFound_shouldThrow() {
        when(notificationRepository.findById("not-found")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationService.dismissNotification("not-found");
        });

        assertEquals("404 NOT_FOUND \"Notification not found\"", exception.getMessage());
    }

    @Test
    void testDeleteNotification_success() {
        when(notificationRepository.existsById("notif-123")).thenReturn(true);

        notificationService.deleteNotification("notif-123");

        verify(notificationRepository).deleteById("notif-123");
    }

    @Test
    void testDeleteNotification_notFound_shouldThrow() {
        when(notificationRepository.existsById("invalid-id")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationService.deleteNotification("invalid-id");
        });

        assertEquals("404 NOT_FOUND \"Notification not found\"", exception.getMessage());
    }
}
