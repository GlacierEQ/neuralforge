package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing user validation records, used for verification and recovery purposes.
 * Maps to the "user_validations" table.
 *
 * @author Your Name
 * @version 1.0
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_validations")
public class UserValidationEntity {

    /**
     * Unique identifier for the validation record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Reference to the associated user.
     * Allows multiple validation records per user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    /**
     * Code used for validation (e.g., verification or password recovery).
     */
    @Column(nullable = false)
    private int verificationCode;

    /**
     * Timestamp indicating when the validation request was made.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    /**
     * Status indicating if the validation has been completed.
     */
    private Boolean status;

    /**
     * Type of validation (either "recover" or "verify").
     */
    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "id", nullable = false)
    private ValidationTypeEntity type;

    /**
     * Sets default values before persisting the entity.
     */
    @PrePersist
    private void setDefaultValues() {
        this.status = Boolean.FALSE;
    }


}
