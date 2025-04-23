package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a quiz in the system.
 * A quiz belongs to a project and contains multiple questions.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "quizzes")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizEntity {
    
    /**
     * Unique identifier for the quiz.
     * Generated automatically as a UUID string.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * ID of the user who created this quiz.
     */
    @Column(nullable = false)
    private String creatorUserId;
    
    /**
     * Title of the quiz.
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * Description of the quiz.
     */
    @Column(length = 1000)
    private String description;

    /**
     * The project this quiz belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;
    
    /**
     * List of questions in this quiz.
     * When a quiz is deleted, all associated questions will also be removed.
     */
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestionEntity> questions = new ArrayList<>();
    
    /**
     * Timestamp representing when the quiz was created.
     * Automatically set when the entity is persisted.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    /**
     * Timestamp representing when the quiz was last modified.
     * Updated automatically whenever the entity is changed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_at", nullable = false)
    private Date lastModifiedAt;
    
    /**
     * Flag to mark if the quiz has been deleted.
     * Soft deletion is used to maintain historical data.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * Lifecycle callback triggered before this entity is persisted to the database.
     * Sets the creation and last modified timestamps to the current date and time.
     */
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.lastModifiedAt = now;
    }

    /**
     * Lifecycle callback triggered before this entity is updated in the database.
     * Updates the last modified timestamp to the current date and time.
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = new Date();
    }
}
