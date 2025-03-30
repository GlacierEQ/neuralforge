package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link NotificationEntity} persistence.
 * Provides methods for CRUD operations and querying by user.
 *
 * @author
 * @version 1.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {

    /**
     * Retrieves all notifications for a given user ID.
     *
     * @param userId The ID of the user.
     * @return A list of {@link NotificationEntity} associated with the user.
     */
    List<NotificationEntity> findByUserId(String userId);
}
