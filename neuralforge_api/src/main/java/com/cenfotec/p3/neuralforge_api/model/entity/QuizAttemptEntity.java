package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a user's attempt to complete a quiz.
 * Tracks the quiz, user, completion time, and score.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "quiz_attempts")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptEntity {
    
    /**
     * Unique identifier for the quiz attempt.
     * Generated automatically as a UUID string.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    /**
     * The quiz that was attempted.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizEntity quiz;
    
    /**
     * The user who attempted the quiz.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_quizattempt_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;
    
    /**
     * The total score achieved in this attempt.
     * Represented as the number of correct answers.
     */
    private int score;
    
    /**
     * The total number of questions in the quiz at the time of the attempt.
     */
    @Column(name = "total_questions")
    private int totalQuestions;
    
    /**
     * List of answers selected by the user in this attempt.
     * When an attempt is deleted, all associated answer selections will also be removed.
     */
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private List<QuizUserAnswerEntity> userAnswers = new ArrayList<>();
    
    /**
     * Timestamp when the quiz attempt was started.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started_at", nullable = false)
    private Date startedAt;
    
    /**
     * Timestamp when the quiz attempt was completed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_at")
    private Date completedAt;
    
    /**
     * Lifecycle callback triggered before this entity is persisted to the database.
     * Sets the start time to the current date and time if not already set.
     */
    @PrePersist
    protected void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = new Date();
        }
    }
}
