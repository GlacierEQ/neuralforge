package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a user's answer to a specific question in a quiz attempt.
 * Tracks which answer the user selected for each question.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "quiz_user_answers")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserAnswerEntity {
    
    /**
     * Unique identifier for the user answer record.
     * Generated automatically as a UUID string.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    /**
     * The quiz attempt this answer belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttemptEntity attempt;
    
    /**
     * The question being answered.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestionEntity question;
    
    /**
     * The answer selected by the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id", nullable = false)
    private QuizAnswerEntity selectedAnswer;
    
    /**
     * Indicates if the selected answer was correct.
     */
    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;
}
