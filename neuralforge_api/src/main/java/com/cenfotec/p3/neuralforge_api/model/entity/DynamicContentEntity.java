package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing dynamic content in the system.

 *
 * @author Fabian Vargas
 * @version 1.0
 */
@Data
@Table(name = "dynamic_content")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicContentEntity {

    /**
     * Unique identifier for the content.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Title of the content.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Timestamp indicating when the content was created.
     * This value is automatically generated and cannot be updated.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * Path where the content is stored.
     */
    @Column(nullable = false)
    private String path;

    /**
     */
    @Column(nullable = false)
    private String email;

    /**
     * Type of the content.
     */
    @Column(nullable = false)
    private String type;
}