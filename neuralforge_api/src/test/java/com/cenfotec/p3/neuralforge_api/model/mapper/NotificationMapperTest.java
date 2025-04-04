package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.NotificationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

    @InjectMocks
    private NotificationMapper notificationMapper;

    private NotificationEntity mockEntity;
    private NotificationResource mockResource;
    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        String userId = UUID.randomUUID().toString();
        mockUser = UserEntity.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();

        mockEntity = NotificationEntity.builder()
                .id(UUID.randomUUID().toString())
                .user(mockUser)
                .title("Test Title")
                .description("Test Description")
                .actionLabel("View")
                .redirectTo("/dashboard")
                .dismissed(false)
                .build();

        mockResource = NotificationResource.builder()
                .id(mockEntity.getId())
                .userId(userId)
                .title("Test Title")
                .description("Test Description")
                .actionLabel("View")
                .redirectTo("/dashboard")
                .dismissed(false)
                .build();
    }

    @Test
    void givenNotificationEntity_whenMapToResource_thenReturnNotificationResource() {
        // When
        NotificationResource result = notificationMapper.mapToResource(mockEntity);

        // Then
        assertNotNull(result);
        assertEquals(mockEntity.getId(), result.getId());
        assertEquals(mockUser.getId(), result.getUserId());
        assertEquals(mockEntity.getTitle(), result.getTitle());
        assertEquals(mockEntity.getDescription(), result.getDescription());
        assertEquals(mockEntity.getActionLabel(), result.getActionLabel());
        assertEquals(mockEntity.getRedirectTo(), result.getRedirectTo());
        assertEquals(mockEntity.getDismissed(), result.getDismissed());
    }

    @Test
    void givenNotificationResourceAndUser_whenMapToEntity_thenReturnNotificationEntity() {
        // When
        NotificationEntity result = notificationMapper.mapToEntity(mockResource, mockUser);

        // Then
        assertNotNull(result);
        assertEquals(mockResource.getId(), result.getId());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockResource.getTitle(), result.getTitle());
        assertEquals(mockResource.getDescription(), result.getDescription());
        assertEquals(mockResource.getActionLabel(), result.getActionLabel());
        assertEquals(mockResource.getRedirectTo(), result.getRedirectTo());
        assertEquals(mockResource.getDismissed(), result.getDismissed());
    }
}
