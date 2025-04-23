package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

/**
 * Resource class representing the data transfer object for a quiz attempt.
 * Contains all necessary attributes for client-server communication.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResource {
    
    /**
     * Unique identifier for the quiz attempt.
     */
    private String id;
    
    /**
     * The quiz ID that was attempted.
     */
    private String quizId;
    
    /**
     * The title of the quiz (for display purposes).
     */
    private String quizTitle;
    
    /**
     * The user ID who attempted the quiz.
     */
    private String userId;
    
    /**
     * The total score achieved in this attempt.
     */
    private int score;
    
    /**
     * The total number of questions in the quiz.
     */
    private int totalQuestions;
    
    /**
     * List of answers selected by the user in this attempt.
     */
    private List<QuizUserAnswerResource> userAnswers;
    
    /**
     * Timestamp when the quiz attempt was started.
     */
    private Date startedAt;
    
    /**
     * Timestamp when the quiz attempt was completed.
     */
    private Date completedAt;
}
