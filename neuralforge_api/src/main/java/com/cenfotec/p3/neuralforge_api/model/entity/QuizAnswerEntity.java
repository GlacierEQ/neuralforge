package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing an answer option for a quiz question.
 * Each answer belongs to a question and can be marked as correct or incorrect.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "quiz_answers")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerEntity {
    
    /**
     * Unique identifier for the answer.
     * Generated automatically as a UUID string.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    /**
     * The text content of the answer option.
     */
    @Column(nullable = false, length = 500)
    private String answerText;
    
    /**
     * The question this answer belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestionEntity question;
    
    /**
     * Indicates if this answer is the correct one for the question.
     * Each question should have exactly one correct answer.
     */
    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;
    
    /**
     * Index determining the order of answers within a question.
     */
    @Column(name = "answer_order")
    private int answerOrder;
}
