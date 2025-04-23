package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a question in a quiz.
 * Each question belongs to a quiz and contains multiple answer options.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "quiz_questions")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionEntity {
    
    /**
     * Unique identifier for the question.
     * Generated automatically as a UUID string.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    /**
     * The text content of the question.
     */
    @Column(nullable = false, length = 1000)
    private String questionText;
    
    /**
     * The quiz this question belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizEntity quiz;
    
    /**
     * List of possible answers for this question.
     * When a question is deleted, all associated answers will also be removed.
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswerEntity> answers = new ArrayList<>();
    
    /**
     * Index determining the order of questions within a quiz.
     */
    @Column(name = "question_order")
    private int questionOrder;
    
    /**
     * Explanation text that explains why the correct answer is correct.
     * This is shown to users after they answer the question incorrectly.
     */
    @Column(name = "explanation", length = 1000)
    private String explanation;
}
