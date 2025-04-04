package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing dynamic content in the system.
 * This entity is used to store content information such as title, creation date,
 * file path, user email, and content type.
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
     * The ID is generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Title of the content.
     * This field is mandatory and cannot be null.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Timestamp indicating when the content was created.
     * This field is automatically set upon entity creation and cannot be updated.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * Path where the content is stored.
     * This field is mandatory and cannot be null.
     */
    @Column(nullable = false)
    private String path;

    /**
     * The email of the user associated with the dynamic content.
     * This field is mandatory and cannot be null.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Type of the content.
     * This field is mandatory and cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DynamicContentTypeEnum type;
}