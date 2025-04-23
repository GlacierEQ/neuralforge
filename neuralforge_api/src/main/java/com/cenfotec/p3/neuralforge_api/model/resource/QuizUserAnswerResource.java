package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Resource class representing the data transfer object for a user's answer to a quiz question.
 * Contains all necessary attributes for client-server communication.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserAnswerResource {
    
    /**
     * Unique identifier for the user answer record.
     */
    private String id;
    
    /**
     * The quiz attempt ID this answer belongs to.
     */
    private String attemptId;
    
    /**
     * The question ID being answered.
     */
    private String questionId;
    
    /**
     * The question text (for display purposes).
     */
    private String questionText;
    
    /**
     * The answer ID selected by the user.
     */
    private String selectedAnswerId;
    
    /**
     * The text of the selected answer (for display purposes).
     */
    private String selectedAnswerText;
    
    /**
     * Indicates if the selected answer was correct.
     */
    private boolean isCorrect;
    
    /**
     * Explanation of the correct answer.
     */
    private String explanation;
    
    /**
     * The ID of the correct answer (useful when user selects wrong answer).
     */
    private String correctAnswerId;
    
    /**
     * The text of the correct answer (useful when user selects wrong answer).
     */
    private String correctAnswerText;
}
